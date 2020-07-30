document.onload = function(){
    let imgs = document.querySelectorAll('.resized-image');
    console.log(imgs);

    for(let img of imgs){
        img.setAttribute('width', '150');
        if(img.clientHeight > 150){
            img.removeAttribute('width');
            img.setAttribute('height', '150');
        }
    }
}