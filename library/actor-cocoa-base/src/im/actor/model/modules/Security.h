//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/ex3ndr/Develop/actor-model/library/actor-cocoa-base/build/java/im/actor/model/modules/Security.java
//

#ifndef _ImActorModelModulesSecurity_H_
#define _ImActorModelModulesSecurity_H_

@class AMRpcException;
@class ImActorModelApiRpcResponseGetAuthSessions;
@class ImActorModelApiRpcResponseVoid;
@class ImActorModelModulesModules;
@protocol AMCommandCallback;

#include "J2ObjC_header.h"
#include "im/actor/model/concurrency/Command.h"
#include "im/actor/model/modules/BaseModule.h"
#include "im/actor/model/network/RpcCallback.h"
#include "java/lang/Runnable.h"

@interface ImActorModelModulesSecurity : ImActorModelModulesBaseModule {
}

- (instancetype)initWithImActorModelModulesModules:(ImActorModelModulesModules *)modules;

- (id<AMCommand>)loadSessions;

- (id<AMCommand>)terminateAllSessions;

- (id<AMCommand>)terminateSessionWithInt:(jint)id_;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity)

@interface ImActorModelModulesSecurity_$1 : NSObject < AMCommand > {
}

- (void)startWithAMCommandCallback:(id<AMCommandCallback>)callback;

- (instancetype)initWithImActorModelModulesSecurity:(ImActorModelModulesSecurity *)outer$;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$1)

@interface ImActorModelModulesSecurity_$1_$1 : NSObject < AMRpcCallback > {
}

- (void)onResultWithImActorModelNetworkParserResponse:(ImActorModelApiRpcResponseGetAuthSessions *)response;

- (void)onErrorWithAMRpcException:(AMRpcException *)e;

- (instancetype)initWithImActorModelModulesSecurity_$1:(ImActorModelModulesSecurity_$1 *)outer$
                                 withAMCommandCallback:(id<AMCommandCallback>)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$1_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$1_$1)

@interface ImActorModelModulesSecurity_$1_$1_$1 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$1_$1:(ImActorModelModulesSecurity_$1_$1 *)outer$
            withImActorModelApiRpcResponseGetAuthSessions:(ImActorModelApiRpcResponseGetAuthSessions *)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$1_$1_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$1_$1_$1)

@interface ImActorModelModulesSecurity_$1_$1_$2 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$1_$1:(ImActorModelModulesSecurity_$1_$1 *)outer$
                                       withAMRpcException:(AMRpcException *)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$1_$1_$2)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$1_$1_$2)

@interface ImActorModelModulesSecurity_$2 : NSObject < AMCommand > {
}

- (void)startWithAMCommandCallback:(id<AMCommandCallback>)callback;

- (instancetype)initWithImActorModelModulesSecurity:(ImActorModelModulesSecurity *)outer$;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$2)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$2)

@interface ImActorModelModulesSecurity_$2_$1 : NSObject < AMRpcCallback > {
}

- (void)onResultWithImActorModelNetworkParserResponse:(ImActorModelApiRpcResponseVoid *)response;

- (void)onErrorWithAMRpcException:(AMRpcException *)e;

- (instancetype)initWithImActorModelModulesSecurity_$2:(ImActorModelModulesSecurity_$2 *)outer$
                                 withAMCommandCallback:(id<AMCommandCallback>)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$2_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$2_$1)

@interface ImActorModelModulesSecurity_$2_$1_$1 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$2_$1:(ImActorModelModulesSecurity_$2_$1 *)outer$;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$2_$1_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$2_$1_$1)

@interface ImActorModelModulesSecurity_$2_$1_$2 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$2_$1:(ImActorModelModulesSecurity_$2_$1 *)outer$
                                       withAMRpcException:(AMRpcException *)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$2_$1_$2)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$2_$1_$2)

@interface ImActorModelModulesSecurity_$3 : NSObject < AMCommand > {
}

- (void)startWithAMCommandCallback:(id<AMCommandCallback>)callback;

- (instancetype)initWithImActorModelModulesSecurity:(ImActorModelModulesSecurity *)outer$
                                            withInt:(jint)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$3)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$3)

@interface ImActorModelModulesSecurity_$3_$1 : NSObject < AMRpcCallback > {
}

- (void)onResultWithImActorModelNetworkParserResponse:(ImActorModelApiRpcResponseVoid *)response;

- (void)onErrorWithAMRpcException:(AMRpcException *)e;

- (instancetype)initWithImActorModelModulesSecurity_$3:(ImActorModelModulesSecurity_$3 *)outer$
                                 withAMCommandCallback:(id<AMCommandCallback>)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$3_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$3_$1)

@interface ImActorModelModulesSecurity_$3_$1_$1 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$3_$1:(ImActorModelModulesSecurity_$3_$1 *)outer$;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$3_$1_$1)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$3_$1_$1)

@interface ImActorModelModulesSecurity_$3_$1_$2 : NSObject < JavaLangRunnable > {
}

- (void)run;

- (instancetype)initWithImActorModelModulesSecurity_$3_$1:(ImActorModelModulesSecurity_$3_$1 *)outer$
                                       withAMRpcException:(AMRpcException *)capture$0;

@end

J2OBJC_EMPTY_STATIC_INIT(ImActorModelModulesSecurity_$3_$1_$2)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

J2OBJC_TYPE_LITERAL_HEADER(ImActorModelModulesSecurity_$3_$1_$2)

#endif // _ImActorModelModulesSecurity_H_
