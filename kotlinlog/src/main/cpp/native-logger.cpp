// Java
#include <jni.h>

// C++
#include <csignal>
#include <cstdio>
#include <cstring>
#include <exception>
#include <memory>
#include <string>
#include <time.h>

// C++ ABI
#include <cxxabi.h>

// Android
#include <android/log.h>
#include <unistd.h>

#define sizeofa(array) sizeof(array) / sizeof(array[0])

#define __NR_tgkill 270

static const int SIGNALS_TO_CATCH[] = {
        SIGABRT,
        SIGBUS,
        SIGFPE,
        SIGSEGV,
        SIGILL,
        SIGSTKFLT,
        SIGTRAP,
};

struct CrashInContext {
    struct sigaction old_handlers[NSIG];
};
typedef void (*CrashSignalHandler)(int, siginfo*, void*);
static CrashInContext* crashInContext = nullptr;

static const char* createCrashMessage(int signo, siginfo* siginfo) {
    void* current_exception = __cxxabiv1::__cxa_current_primary_exception();
    std::type_info* current_exception_type_info = __cxxabiv1::__cxa_current_exception_type();

    size_t buffer_size = 1024;
    char* abort_message = static_cast<char*>(malloc(buffer_size));

    if (current_exception) {
        try {
            if (current_exception_type_info) {
                const char* exception_name = current_exception_type_info->name();
                int status = -1;
                char demangled_name[buffer_size];
                __cxxabiv1::__cxa_demangle(exception_name, demangled_name, &buffer_size, &status);

                if (status) {
                    sprintf(abort_message, "Terminating with uncaught exception of type %s", exception_name);
                } else {
                    if (strstr(demangled_name, "nullptr") || strstr(demangled_name, "NULL")) {
                        sprintf(abort_message, "Terminating with uncaught exception of type %s", demangled_name);
                    } else {
                        try {
                            __cxxabiv1::__cxa_rethrow_primary_exception(current_exception);
                        } catch (std::exception& e) {
                            sprintf(abort_message, "Terminating with uncaught exception of type %s : %s", demangled_name, e.what());
                        } catch (...) {
                            sprintf(abort_message, "Terminating with uncaught exception of type %s", demangled_name);
                        }
                    }
                }

                return abort_message;
            } else {
            }
        }
        catch (std::bad_cast& bc) {
        }
    }
    sprintf(abort_message, "Terminating with a C crash %d : %d", signo, siginfo->si_code);
    return abort_message;
}

/* return current time in milliseconds */
static double now_ms(void) {
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;
}

const char* path = nullptr;
static void nativeCrashSignalHandler(int signo, siginfo* siginfo, void* ctxvoid) {
    sigaction(signo, &crashInContext->old_handlers[signo], nullptr);

    // Current date/time based on current system
    time_t now = time(0);

    // Convert now to tm struct for local timezone
    tm* localtm = localtime(&now);

    std::string errorFilePath = path;
    errorFilePath.append("/");
    errorFilePath.append(asctime(localtm));
    errorFilePath.append("-unhandledNativeCrash.log");

    char cstr[errorFilePath.size() + 1];
    strcpy(cstr, errorFilePath.c_str());

    FILE* file = fopen(cstr,"w+");

    if (file != NULL)
    {
        fputs(createCrashMessage(signo, siginfo), file);
        fflush(file);
        fclose(file);
    }
    if (siginfo->si_code <= 0 || signo == SIGABRT) {
        if (syscall(__NR_tgkill, getpid(), gettid(), signo) < 0) {
            _exit(1);
        }
    }
}

static bool registerSignalHandler(CrashSignalHandler handler, struct sigaction old_handlers[NSIG]) {
    struct sigaction sigactionstruct;
    memset(&sigactionstruct, 0, sizeof(sigactionstruct));
    sigactionstruct.sa_flags = SA_SIGINFO;
    sigactionstruct.sa_sigaction = handler;

    // Register new handlers for all signals
    for (int index = 0; index < sizeofa(SIGNALS_TO_CATCH); ++index) {
        const int signo = SIGNALS_TO_CATCH[index];

        if (sigaction(signo, &sigactionstruct, &old_handlers[signo])) {
            return false;
        }
    }

    return true;
}

static void unregisterSignalHandler(struct sigaction old_handlers[NSIG]) {
    // Recover old handler for all signals
    for (int signo = 0; signo < NSIG; ++signo) {
        const struct sigaction* old_handler = &old_handlers[signo];

        if (!old_handler->sa_handler) {
            continue;
        }

        sigaction(signo, old_handler, nullptr);
    }
}

static bool deinitializeNativeCrashHandler() {

    if (!crashInContext) return false;

    unregisterSignalHandler(crashInContext->old_handlers);

    free(crashInContext);
    crashInContext = nullptr;

    __android_log_print(ANDROID_LOG_ERROR, "NDK Logger", "%s", "Native crash handler successfully deinitialized.");

    return true;
}

/// like TestFairy.begin() but for crashes
static void initializeNativeCrashHandler() {

    if (crashInContext) {
        __android_log_print(ANDROID_LOG_INFO, "NDK Logger", "%s", "Native crash handler is already initialized.");
        return;
    }

    crashInContext = static_cast<CrashInContext *>(malloc(sizeof(CrashInContext)));
    memset(crashInContext, 0, sizeof(CrashInContext));

    if (!registerSignalHandler(&nativeCrashSignalHandler, crashInContext->old_handlers)) {
        deinitializeNativeCrashHandler();
        __android_log_print(ANDROID_LOG_ERROR, "NDK Logger", "%s", "Native crash handler initialization failed.");
        return;
    }

    __android_log_print(ANDROID_LOG_ERROR, "Logger", "%s", "Native crash handler successfully initialized.");
}

/// Jni bindings

extern "C" JNIEXPORT jstring JNICALL
Java_quanti_com_kotlinlog_Log_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}



extern "C" JNIEXPORT void JNICALL
Java_quanti_com_kotlinlog_Log_init(
        JNIEnv* env,
        jobject obj,
        jstring pathh) {
    path = env->GetStringUTFChars(pathh, 0);
    initializeNativeCrashHandler();
}

extern "C" JNIEXPORT void JNICALL
Java_quanti_com_kotlinlog_Log_deiniti(
        JNIEnv* env,
        jobject /* this */) {
    deinitializeNativeCrashHandler();
}