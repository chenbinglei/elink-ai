export function hexToRgba(color, alpha) {
    if (!color) {
        return "rgba(0,0,0,1)"
    }
    let c1 = parseInt(color.substr(1, 2), 16);
    let c2 = parseInt(color.substr(3, 2), 16);
    let c3 = parseInt(color.substr(5, 2), 16);
    return `rgba(${c1},${c2},${c3},${alpha})`;
}