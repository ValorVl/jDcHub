/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 09.12.11
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(
    function(){

        $(".execute").click(function(){



            $.ajax( {
                type: "POST",
                url: "../modulemanagement",
                data: { selected: $(".selected").val() || [],action:$("select").val(),state:$(".state").val() },
                success: function(data) {

                }
            })

        })

    }
)