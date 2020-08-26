let img = document.createElement('img');
img.src = 'img/drips.png';
img.width = document.body.clientWidth - 250;
img.height = document.body.clientHeight * 1.5;
img.style.zIndex = 99;
img.style.left = '250px';
img.style.position = 'absolute';

let iterate = 5;

export function createData(formId = 'default', startObject = {}){
    let object = {};

    if(formId != 'default' && formId != null){
        let form = document.getElementById(formId);
        let names = new Set();
        for(let i = 0; i < form.elements.length; i++){
            let input = form.elements[i];
            if(!(input.type == 'button' || input.tagName == 'BUTTON'))
                names.add(input.name)
        }
        names.forEach(name => object[name] = form[name].value)
    }
    if(object.login == null || object.login == ''){
        object.login = content.login;
        object.password = content.password;
        object.id = content.id;
    }

    Object.assign(object, startObject);
    return object;
}

export function fetchPage(page, formId = 'default', startObject = {}, nextMethod = (function(){}), setPage = 'default'){
    swipe(() => {
        let obj = createData(formId, startObject);
        fetch(page, {
            method: 'POST',
            body: JSON.stringify(obj)
        })
        .then(data => {
            window.test = data;
            return data;
        })
        .then(data => data.json())
        .then(data => {
            window.test1 = data;
            return data;
        })
        .then(data => window.content.data = data)
        .then(data => window.content.site = (setPage === 'default' ? page : setPage))
        .then(setTimeout(nextMethod, 10))
        .catch(error => {});
    })
}

export function swipe(func){
    if(window.isActive){
        console.log('continue without animation');
        func();
    }
    else {
        window.isActive = true;
        document.body.append(img);
        img.style.top = -img.height + 'px';
        window.i = parseInt(img.style.top);
        let interval = setInterval(() => {
            img.style.top = window.i + 'px';
            window.i += iterate
        }, 1);
        setTimeout(() => {
            clearInterval(interval);
            func();
            window.i = 100;
            interval = setInterval(() => {
                img.style.opacity = (--window.i / 100);
            }, 2)
            setTimeout(() => {
                clearInterval(interval);
                window.i = 0;
                img.style.top = -img.height + 'px';
                img.style.opacity = '1.0';
                window.isActive = false;
            }, 200)
        }, img.height / iterate * 4 - 2);
    }
}