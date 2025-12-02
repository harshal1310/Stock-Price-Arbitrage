// src/Login.js
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function Login({ onLogin }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const handleLogin = async () => {
        try {
            const res = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
                credentials: "include"
            });

            if (res.ok) {
                const data = await res.json();
                console.log("cookies : ", document.cookie)
                console.log("Login successful:", data);
                setMessage("Login successful!");

               // localStorage.setItem("user", JSON.stringify(data));
                localStorage.setItem("token", data.token); // store only the JWT

                // redirect
                navigate("/stocks");

                onLogin(data);
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
            <h2>Login</h2>
            <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
            <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
            <button onClick={handleLogin}>Login</button>
            {message && <p>{message}</p>}
        </div>
    );
}

export default Login;
