jQuery.expr[':'].Contains = function (a, i, m) {
    return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
};

function filterList(list) {
    $('#js-groupId').on('input propertychange', function () {
        var filter = $(this).val();
        this.value=this.value.replace(' ','');
        if (filter) {
            $matches = $(list).find('a:Contains(' + filter + ')').parent();
            $('li', list).not($matches).slideUp();
            $matches.css("display","-webkit-box");
            $matches.slideDown();
        } else {
            $(list).find("li").slideUp();
        }
    });
}
$(function () {
    $('.searchIn,.cancle').click(function(){
        $('.search-val-inner,.allHide').toggle();
    })
    $('.search-value').on('click',function() {
        $('.search').hide();
    })
    $('.search-value').on('blur',function() {
      var len = $(this).val();
      if(len == ''){
        $('.search').show();
      }
    })
    filterList($("#groupid"));
    $('#js-groupId').bind('focus', function () {
        $('#groupid').slideDown();
    }).bind('blur', function () {
        $('#groupid').slideUp();
    })

})