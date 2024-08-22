$(document).ready(function() {
    $("#encryptForm").submit(function(event) {
        event.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            type: "POST",
            url: "/encrypt",
            data: formData,
            success: function(response) {
                $("#encryptedText").text(response.encryptedText);
                $("#keyMatrix").text(response.keyMatrixString);
            }
        });
    });
});
