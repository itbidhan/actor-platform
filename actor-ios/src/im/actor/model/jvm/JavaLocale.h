//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/ex3ndr/Develop/actor-model/actor-ios/build/java/im/actor/model/jvm/JavaLocale.java
//

#ifndef _AMJavaLocale_H_
#define _AMJavaLocale_H_

@class JavaUtilHashMap;

#include "J2ObjC_header.h"
#include "im/actor/model/LocaleProvider.h"

@interface AMJavaLocale : NSObject < AMLocaleProvider > {
}

- (instancetype)initWithNSString:(NSString *)name;

- (JavaUtilHashMap *)loadLocale;

- (jboolean)is24Hours;

@end

J2OBJC_EMPTY_STATIC_INIT(AMJavaLocale)

CF_EXTERN_C_BEGIN
CF_EXTERN_C_END

typedef AMJavaLocale ImActorModelJvmJavaLocale;

J2OBJC_TYPE_LITERAL_HEADER(AMJavaLocale)

#endif // _AMJavaLocale_H_
