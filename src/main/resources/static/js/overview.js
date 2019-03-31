let spinner;
$(document).ready(function () {

    moment.locale('de');
    retrieveTasks();
});

function retrieveTasks() {
    console.log("retrieve Data method...");

    $("a").on("click", function(e){
        var link = $(this);
        link.preventDefault();

        // att href
        // wenn href === "details"
        // details();
    });

    $("#spinner").show();
    $.ajax({
        type: "GET",
        url: "/api/tasks",
        datatype: "json",

        success: function (result) {
            console.log("Ajax-Success method.");
            console.dir(result.length);

            if (result.length !== 0) {
                $.each(result, function (i, task) {

                    var taskTemplate = $("<tr class='table-row' id='rowId" + i + "'>" +
                        "<td> " + (i + 1) + " </td>" +
                        "<td><a href='/tasks/" + task.id + "'>" + task.title + "</a></td>" +
                        "<td>" + moment(task.created).format("L LTS") + "</td>" +
                        "<td>" + moment(task.finishDate).format("dddd LL") + "</td>" +
                        "<td>" +
                        "   <form action='/tasks/task/complete/" + task.id + "' method='post'>" +
                        "       <button type='submit' class='btn btn-sm btn-primary' style='text-align: center'>" +
                        "           <i class='fas align-middle doneImg' id='doneImgParent" + i + "'></i>" +
                        "       </button>" +
                        "       <a href='/tasks/task/" + task.id + "'>" +
                        "           <i class='fas fa-edit align-middle' style='font-size: 22px; padding-right: 10px;'></i>" +
                        "       </a>" +
                        "       <a href='/tasks/delete/" + task.id + "'" +
                        "           onclick='return confirm('Are you sure you want to delete this item?');'>" +
                        "           <i class='fas fa-trash-alt align-middle' style='font-size: 22px; padding-right: 10px;'></i>" +
                        "       </a>" +
                        "   </form>" +

                        "" +
                        "</td>" +
                        "</tr>");

                    $("#tbody-tasks").append(taskTemplate);
                    if (task.done) {
                        console.log("in if clause of task.");
                        $("#doneImgParent" + i).addClass('fa-ban');
                        taskTemplate.addClass('table-success');
                    } else {
                        $("#doneImgParent" + i).addClass('fa-check');

                    }

                    $.each(task.children, function (j, child) {


                        var taskTemplate1 = $("<tr class='table-row' id='rowId" + j + "'>" +
                            "<td>" + (i + 1) + "." + (j + 1) + "</td>" +
                            "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='/tasks/" + child.id + "'>" + child.title + "</a></td>" +
                            "<td>" + moment(child.created).format("L LTS") + "</td>" +
                            "<td>" + moment(child.finishDate).format("dddd LL") + "</td>" +
                            "<td>" +
                            "   <form action='/tasks/task/complete/" + child.id + "' method='post'>" +
                            "       <button type='submit' class='btn btn-sm btn-primary' style='text-align: center'>" +
                            "           <i class='fas align-middle doneImg'id='doneImgChild" + j + "'></i>" +
                            "       </button>" +
                            "       <a href='/tasks/task/" + child.id + "'><i class='fas fa-edit align-middle'" +
                            "           style='font-size: 22px; padding-right: 10px;'></i>" +
                            "       </a>" +
                            "       <a href=''/tasks/delete/" + task.id + "'" +
                            "           onclick='return confirm('Are you sure you want to delete this item?');'>" +
                            "           <i class='fas fa-trash-alt align-middle' style='font-size: 22px; padding-right: 10px;'></i>" +
                            "       </a>" +
                            "   </form>" +
                            "</td>" +
                            "</tr>");

                        console.log(child.done);
                        $("#tbody-tasks").append(taskTemplate1);
                        if (child.done) {
                            console.log("in if clause of child.");
                            $("#doneImgChild" + j).addClass('fa-ban');
                            taskTemplate1.addClass('table-success');
                        } else {
                            $("#doneImgChild" + j).addClass('fa-check');
                        }

                    })
                });
            }
            $("#spinner").hide();
            $("#taskTableContent").show();
        }
    });
}

function spinning() {

    const opts = {
        lines: 13, // The number of lines to draw
        length: 38, // The length of each line
        width: 17, // The line thickness
        radius: 45, // The radius of the inner circle
        scale: 1.05, // Scales overall size of the spinner
        corners: 1, // Corner roundness (0..1)
        color: '#ffffff', // CSS color or array of colors
        fadeColor: 'transparent', // CSS color or array of colors
        speed: 1, // Rounds per second
        rotate: 0, // The rotation offset
        animation: 'spinner-line-fade-quick', // The CSS animation name for the lines
        direction: 1, // 1: clockwise, -1: counterclockwise
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        className: 'spinner', // The CSS class to assign to the spinner
        top: '50%', // Top position relative to parent
        left: '50%', // Left position relative to parent
        shadow: '0 0 1px transparent', // Box-shadow for the lines
        position: 'absolute' // Element positioning
    };
    spinner = new Spinner(opts);
}