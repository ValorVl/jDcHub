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

        $("#del").click(function ()
                        {
                            var blabla = new Array();
                            $('input[name="ids[]"]').each(function(){
                                if($(this).is(':checked')) blabla.push($(this).val());
                            });
                            $.ajax({
                                       type:"POST",
                                       url:"../bwdel",
                                       data:{ "id": blabla },
                                       success:function (data)
                                       {
                                           $("#out").html(data);
                                           for(var i=0; i<blabla.length; i++) {
                                               $('#rules tr[data-id=' +blabla[i] + ']').fadeOut();
                                           }
                                       }
                                   })
                            return false;
                        })
    })