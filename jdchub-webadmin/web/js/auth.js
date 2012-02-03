/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 09.12.11
 * Time: 7:10
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(
    function(){
        $("#enter").click(function() {
            $.ajax({
                       type: "POST",
                       url: "../auth",
                       data: { login: $("#login").val(),pwd:$("#pwd").val() },
                       success: function(data) {
                           $("#mess").html(data).fadeIn("slow")
                       }
                   })
        })
    }
)