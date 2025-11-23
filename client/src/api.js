export async function addStock(symbol) {
    const res = await fetch(`/api/addstock?symbol=${symbol}`, {
        method: "POST"
    });
    return res.json();
}

export async function getPrices() {
    const res = await fetch("/api/prices");
    return res.json();
}
