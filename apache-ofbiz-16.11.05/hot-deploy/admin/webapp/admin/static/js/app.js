
export const App = {
    unescapeHtmlText: function (text) {
        return jQuery('<div />').html(text).text()
    }
};
window.App = App;