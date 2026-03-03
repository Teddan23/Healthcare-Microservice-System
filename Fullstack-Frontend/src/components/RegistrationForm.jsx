import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const RegistrationForm = () => {
    const [personnummer, setPersonnummer] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [gender, setGender] = useState('male');
    const [birthDate, setBirthDate] = useState('');
    const [role, setRole] = useState('patient');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const { register } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const personnummerPattern = /^\d{8}-\d{4}$/;

        // Validate personnummer format
        if (!personnummerPattern.test(personnummer)) {
            setErrorMessage('Personnummer måste vara i formatet YYYYMMDD-XXXX');
            return;
        }

        const userDTO = {
            personnummer,
            firstName,
            lastName,
            gender,
            birthDate,
            role,
            password
        };

        try {
            const response = await axios.post(`https://fullstack-userservice.app.cloud.cbh.kth.se/user/register`, userDTO, {
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            console.log(response.data);
            alert(response.data);
            navigate("/login");
        } catch (error) {
            if (error.response) {
                console.error('Error:', error.response.data);
                alert(error.response.data);
            } else {
                console.error('Error:', error.message);
                alert('An unknown error occurred. Please try again.');
            }
        }
    };

    return (
        <div>
            <h1>Register for Patient Journal System</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <input
                        type="text"
                        placeholder="Personnummer (YYYYMMDD-XXXX)"
                        value={personnummer}
                        onChange={(e) => setPersonnummer(e.target.value)}
                        required
                        pattern="\d{8}-\d{4}"
                        title="Personnummer måste vara i formatet YYYYMMDD-XXXX"
                    />
                    {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
                </div>
                <div>
                    <input
                        type="text"
                        placeholder="First Name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <input
                        type="text"
                        placeholder="Last Name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Gender:</label>
                    <select value={gender} onChange={(e) => setGender(e.target.value)}>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                    </select>
                </div>
                <div>
                    <label>Birth Date:</label>
                    <input
                        type="date"
                        value={birthDate}
                        onChange={(e) => setBirthDate(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Role:</label>
                    <select value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="patient">Patient</option>
                        <option value="doctor">Doctor</option>
                        <option value="staff">Staff</option>
                    </select>
                </div>
                <div>
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default RegistrationForm;
