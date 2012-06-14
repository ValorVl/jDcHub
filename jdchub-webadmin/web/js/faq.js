/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 07.12.11
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(
    function(){

        $("#reload").click(function(){

            $.ajax( {
                        type: "POST",
                        url: "../btHandler",
                        data: { id: $("#l").val(),action:"send",rsrc:"faq" },
                        success: function(data) {
                            $("#out").hide();
                            $("#out").val(data).fadeIn("slow");
                        }
                    } )
        })

        $("#submit").click(function(){
            $.ajax({
                       type: "POST",
                       url: "../btHandler",
                       data: { body: $("#out").val(), locale: $("#l").val(),action:"update",rsrc:"faq" },
                       success: function(body,locale) {
                          $("#message").html("<h3>Success!</h3>").show()
                          $("#message").fadeOut(3000)
                       }
                   })
        })
    }
)
