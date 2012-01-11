/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 08.12.11
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function() {

    $("#stop").click(function() {
        $.ajax({
           type:"POST",
           url:"../serverManagment",
           data:{ action:"stop"},
           success:function (data)
           {
                $("#mess").html(data);
           }
        })
    })
    $("#restart").click(function() {
        $.ajax({
           type:"POST",
           url:"../serverManagment",
           data:{ action:"restart"},
           success:function (data)
           {
               $("#mess").html(data);
           }
       })
    })
    $("#reload").click(function() {
    $.ajax({
           type:"POST",
           url:"../serverManagment",
           data:{ action:"reload"},
           success:function (data)
           {
               $("#mess").html(data);
           }
       })
    })
});