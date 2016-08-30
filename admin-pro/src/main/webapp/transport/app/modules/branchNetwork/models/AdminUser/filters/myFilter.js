angular.module('adminUser').filter(
    "myLabelUser",
    function () {
        return function (input ) {
            if (input==null){
                return "网点用户";
            }
            return input;
        }
});
angular.module('adminUser').filter(
    "myLabelName",
    function () {
    return function (input ) {
        if (input==1){
            return "在职";
        }
        return "离职";
    }

});
