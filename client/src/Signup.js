// src/Signup.js
import React, { useState } from "react";

function Signup() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleSignup = async () => {
        try {
            const res = await fetch("/api/auth/signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email, password }),
            });

            if (res.ok) {
                setMessage("Signup successful! You can now login.");
                setName(""); setEmail(""); setPassword("");
            } else {
                const text = await res.text();
                setMessage("Error: " + text);
            }
        } catch (err) {
            setMessage("Network error");
        }
    };

    return (
        <div className="auth-container">
            <h2>Signup</h2>
            <input type="text" placeholder="Name" value={name} onChange={e => setName(e.target.value)} />
            <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
            <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
            <button onClick={handleSignup}>Signup</button>
            {message && <p>{message}</p>}
        </div>
    );
}

export default Signup;
