import React, { useEffect, useState } from "react";
import { getAllUsers } from "./api";

function Users() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const data = await getAllUsers();
                setUsers(data);
            } catch (err) {
                setError(err.message || "Failed to fetch users");
            }
        };
        fetchUsers();
    }, []);

    if (error) return <p style={{ color: "red" }}>{error}</p>;

    return (
        <div className="container">
            <h2>All Registered Users</h2>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Roles</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.name}</td>
                        <td>{user.email}</td>
                        <td>{user.roles?.join(", ")}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default Users;
