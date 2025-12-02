export async function addStock(symbol) {
    const token = localStorage.getItem("token");

    const res = await fetch(`/api/addstock?symbol=${symbol}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });
    return res.json();
}

export async function getPrices() {
    const res = await fetch("/api/prices");
    return res.json();
}

export async function signup(user) {
    const res = await fetch("/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user),
    });
    return res.json();
}

export async function login(credentials) {
    const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
    });
    return res.json();
}

export async function getAllUsers() {
    const token = localStorage.getItem("token");

    const res = await fetch("/api/auth/getAllUsers", { method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }

    });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(text);
    }
    return res.json();
}

