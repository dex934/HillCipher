$(document).ready(function() {
	$("#decryptForm").submit(function(event) {
		event.preventDefault();
		var formData = $(this).serialize();
		$.ajax({
			type: "POST",
			url: "/decrypt",
			data: formData,
			success: function(response) {
				$("#decryptedText").text(response.decryptedText);
			}
		});
	});
});
