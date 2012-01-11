/**
 * Created by IntelliJ IDEA.
 * User: valor
 * Date: 26.12.11
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(
    function ()
    {

        $("#out").load("../simples/bwbody.jsp")

        $("#add").click(function ()
           {
           $.ajax({
                      type:"POST",
                      url:"../bw",
                      data:{ regex:$("#regex").val(),action:$("#action").val(),param:$("#param").val() },
                      success:function (data)
                      {
                          $("#out").html(data)
                      }
                  })
           })
    })