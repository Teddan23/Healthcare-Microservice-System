import React, { useEffect, useState } from "react";
import axios from "axios";
import AdminDashboard from "../pages/AdminDashboard.jsx";

const PatientList = () => {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const authToken = sessionStorage.getItem("authToken");

    useEffect(() => {
        axios
            .get(`https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/all`,
                {
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                    },
                })
            .then((response) => {
                console.log("Server Response:", response.data);
                setPatients(response.data);
                setLoading(false);
            })
            .catch((err) => {
                setError("An error occurred while fetching data: " + err.message);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return <div>Loading patients...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    {console.log("Patients before passing to AdminDashboard:", patients)}

    return (
        <div>
            <h1>Patient List</h1>
            {patients.length === 0 ? (
                <p>No patients found.</p>
            ) : (
                <AdminDashboard patients={patients} />
            )}
        </div>
    );

};
export default PatientList;