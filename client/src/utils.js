

const toMap = (array) => {
    return  array.reduce((obj, el) => {
        obj[el.id] = el;
        return obj;
    }, {})
};