
export const HtmlTagsXss = {
    unescapeHtmlText: function (text) {
        return jQuery('<div />').html(text).text()
    }
};
window.HtmlTagsXss = HtmlTagsXss;