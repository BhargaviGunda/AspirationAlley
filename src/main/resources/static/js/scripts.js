// scripts.js
document.addEventListener('DOMContentLoaded', function() {
    const mediaInput = document.getElementById('media');
    const previewContainer = document.getElementById('preview');

    mediaInput.addEventListener('change', function() {
        previewContainer.innerHTML = ''; // Clear previous previews
        const files = mediaInput.files;

        for (const file of files) {
            const reader = new FileReader();
            const fileType = file.type.split('/')[0];

            reader.onload = function(e) {
                const url = e.target.result;
                if (fileType === 'image') {
                    const img = document.createElement('img');
                    img.src = url;
                    previewContainer.appendChild(img);
                } else if (fileType === 'video') {
                    const video = document.createElement('video');
                    video.src = url;
                    video.controls = true;
                    previewContainer.appendChild(video);
                }
            };

            reader.readAsDataURL(file);
        }
    });
});
