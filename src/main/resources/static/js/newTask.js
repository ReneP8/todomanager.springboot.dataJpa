$(function () {
    moment.locale('de');
    retrieveParentTasksForDropDown();
    initSubmitButton();

    function retrieveParentTasksForDropDown() {
        $("#spinner").show();
        $.ajax({
            type: "GET",
            url: "/api/parents",
            datatype: "json",

            success: function (result) {
                var optionTemplate;

                $.each(result, function (i, task) {

                    optionTemplate = $("<option value='" + task.id + "' >" + task.id + " - " + task.title + "</option>");

                    var dropdown = $("#parentTaskID");
                    dropdown.append(optionTemplate);
                })
                $("#spinner").hide();
                $("#newTaskContent").show();
            }
        })
    }

    function initSubmitButton() {
        $("#newTaskForm").submit(function (event) {
                event.preventDefault();
                // $("#spinner").show();
            $("#newTaskSubmit").attr("disabled", "disabled");
            $("#responseMessage").removeClass("alert-danger alert-success").hide();

                $.post("/tasks/new", $("#newTaskForm").serialize())
                    .done(function (response) {
                        console.dir(response);

                        var message = $("<p style='margin-bottom: 0px;'>" + response + "</p>");

                        $("#responseMessage").addClass("alert-success").html(message);
                    })
                    .fail(function (error) {
                        console.dir(error);
                        var message = $("<p style='margin-bottom: 0px;'>" + error.responseText + "</p>");

                        $("#responseMessage").addClass("alert-danger").html(message);
                    })
                    .always(function () {
                        $("#responseMessage").show();
                        setTimeout(function () {
                            $("#responseMessage").hide();
                        }, 3000);
                        $("#newTaskSubmit").attr("disabled", false);
                    })


            //
            }
        );

    }

});