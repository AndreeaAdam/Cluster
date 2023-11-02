package cluser.crm.config;

public enum ResponseEnum {
    success, invalidId, notLoggedIn, uploadImageFailure, wrongExtension,notUnique, duplicateSlug, invalidSlug,
    notAllowed, noResource, notAvailable, alreadyExists, notExists, notGoogleAccount, notFacebookAccount, invalidRole,
    invalidEmail, accountNotActive, badCredentials, authenticationFailed, alreadyLoggedIn, refreshTokenExpired,
    tokenExpired, notInConversation, invalidIdToken, invalidAccessToken, emailExists, notFound, unsentEmail, emailSent,
    invalidAuthCode, authCodeNotExists, fileSizeTooBig, alreadySent
}
