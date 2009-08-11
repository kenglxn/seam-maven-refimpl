// Search helper Singleton object
SearchHelper = ( function() {
    // Private attributes and methods
    var searchHint = 'search...';

    return ({
        // Public attributes and methods

        /**
         * Removes the string 'search...' from the search field if present
         * @method removeHint
         * @param target {HTMLElement} the element to remove the string 'search...' value from
         * @return {void}
         * @public
         */
        removeHint: function(target) {
            if (target && new RegExp('^\\s*' + searchHint + '\\s*', 'i').test(target.value)) {
                target.value = '';
            }
        },

        /**
         * Appends the string 'search...' to the search field if search field value is blank
         * @method appendHint
         * @param target {HTMLElement} the element to append the string 'search...' value to
         * @return {void}
         * @public
         */
        appendHint: function(target) {
            if (target && (/^\s*$/.test(target.value) )) {
                target.value = searchHint;
            }
        }
    }); //~return public attributes and methods
} )(); //~SearchHelper, anonymous function immediately invoked with ()