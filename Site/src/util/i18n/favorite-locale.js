//Souce : https://phrase.com/blog/posts/vue-2-localization/

export default function getFavoriteLocale(options = {}) {
    const defaultOptions = { countryCode: false }
    const opt = { ...defaultOptions, ...options }
    const navigatorLocale =
        navigator.languages !== undefined
            ? navigator.languages[0]
            : navigator.language
    if (!navigatorLocale) {
        return undefined
    }
    const trimmedLocale = opt.countryCodeOnly
    ? navigatorLocale.trim().split(/-|_/)[0]
        : navigatorLocale.trim()
    return trimmedLocale
}