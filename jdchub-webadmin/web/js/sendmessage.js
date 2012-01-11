/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 08.12.11
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(
    function(){
        $("#mtype").change(function() {
            if ($("#mtype").val() == "b"){
                $("#nick").fadeOut("slow")
            }else if ($("#mtype").val() == "p"){
                $("#nick").fadeIn("slow")
            }
        })
        $("#submit").click(function() {
            $.ajax({
               type: "POST",
               url: "../sendMessage",
               data: { type: $("#mtype").val(),nick:$("#nick").val(),message:$("#messagebody").val() },
               success: function(data) {
                   $("#callback").hide()
                   $("#callback").html(data).fadeTo("slow")
               }
            })
        })
    }
)
