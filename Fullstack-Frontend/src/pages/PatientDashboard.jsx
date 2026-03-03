import React, {useState, useEffect, useContext} from "react";
import {AuthContext} from "../context/AuthContext.jsx";
import axios from "axios";

const PatientDashboard = () => {
    const { user } = useContext(AuthContext);
    const [newMessage, setNewMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [patientDetails, setPatientDetails] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false)
    const authToken = sessionStorage.getItem("authToken");

    useEffect(() => {
        if (user && user.personnummer) {
            fetchPatientDetails(user.personnummer);
        }
    }, [user]);

    const fetchPatientDetails = (personnummer) => {
        setLoading(true);
        axios
            .get(`https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/${personnummer}/personDetails`,
                {
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                    },
                }
                )
            .then((response) => {
                console.log("Personnummer: ", user.personnummer);
                console.log("Fetch patient details response: ", response.data);
                const {
                    id,
                    firstName,
                    lastName,
                    gender,
                    birthDate,
                    observations,
                    conditions
                } = response.data;

                const patientData = {
                    id,
                    firstName,
                    lastName,
                    gender,
                    birthDate,
                    observations: observations || [],
                    conditions: conditions || []
                };
                handleFetchMessages(personnummer);
                setPatientDetails(patientData);
                setLoading(false);
            })
            .catch((err) => {
                setError("An error occurred while fetching patient details: " + err.message);
                setLoading(false);
            });
    };

    const handleFetchMessages = (patientPersonnummer) => {
        axios
            .post(`https://fullstack-messageservice.app.cloud.cbh.kth.se/messages/getMessages`, null, {
                params: { patientPersonnummer },
                headers: {
                    Authorization: `Bearer ${authToken}`,
                },
            })
            .then((response) => {
                const messages = response.data;

                const updatedMessages = messages.map((message) => ({
                    ...message,
                    senderFirstName: message.sender.firstName,
                }));

                setMessages(updatedMessages);
                console.log("Messages fetched successfully:", updatedMessages);
            })
            .catch((err) => {
                console.error("Error occurred:", err);
                setError("An error occurred: " + err.message);
            })
            .finally(() => {
                setLoading(false);
            });
    };


    const calculateAge = (dob) => {
        if (!dob) return "N/A";
        const birthDate = new Date(dob);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };

    const handleSendMessage = () => {
        if (newMessage.trim() === "") {
            alert("Message cannot be empty");
            return;
        }

        const currentDateTime = new Date().toISOString();  // Tidsstämpel i ISO-format
        const senderPersonnummer = user.personnummer;      // Avsändarens personnummer (som tas från AuthContext)
        const receiverPersonnummer = user.personnummer; // Mottagarens personnummer (från vald patient)

        const newMessageObject = {
            senderPersonnummer,      // Skicka avsändarens personnummer
            receiverPersonnummer,    // Skicka mottagarens personnummer
            message: newMessage,     // Skicka meddelandet
            timeStamp: currentDateTime, // Skicka tidsstämpel
        };

        axios.post(`https://fullstack-messageservice.app.cloud.cbh.kth.se/messages/sendMessage`, newMessageObject,
            {
                headers: {
                    Authorization: `Bearer ${authToken}`,
                },
            }
        )
            .then(() => {
                console.log("Message sent successfully");

                const updatedPatient = {
                    ...user,
                    messages: [
                        ...(user.messages || []),
                        {
                            from: user.name,      // Skicka avsändarens namn
                            content: newMessage,  // Skicka meddelandet
                            timeStamp: currentDateTime,
                        }
                    ],
                };

                setNewMessage("");
            })
            .catch((error) => {
                console.error("Error sending message:", error);
                alert("Failed to send message: " + error.message);
            });
    };

    if (loading) {
        return <div>Loading patient details...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    if (!patientDetails) {
        return <div>No patient data available.</div>;
    }

    return (
        <div className="patient-dashboard">
            {loading ? (
                <div className="loading">
                    <p>Loading patient details...</p>
                </div>
            ) : error ? (
                <div className="error">
                    <p>{error}</p>
                </div>
            ) : patientDetails ? (
                <div className="patient-details">
                    <h2>Patient Details</h2>
                    <div className="patient-info">
                        <h3>{`${patientDetails.firstName} ${patientDetails.lastName}`}</h3>
                        <p><strong>Personnummer:</strong> {user.personnummer}</p>
                        <p><strong>Age:</strong> {calculateAge(patientDetails.birthDate)}</p>
                        <p><strong>Gender:</strong> {patientDetails.gender}</p>
                    </div>

                    <div className="patient-conditions">
                        <h4>Diagnosis:</h4>
                        {patientDetails.conditions.length > 0 ? (
                            <ul>
                                {patientDetails.conditions.map((condition, index) => (

                                    <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                        <strong>{condition.code}</strong>: {condition.description || "No description available"} <br />
                                        <strong>Onset Date:</strong> {condition.onsetDate ? new Date(condition.onsetDate).toLocaleDateString() : "Not available"}
                                    </div>
                                ))}
                            </ul>
                        ) : (
                            <p>No diagnosis found.</p>
                        )}
                    </div>

                    <div className="patient-observations">
                        <h4>Notes:</h4>
                        {patientDetails.observations.length > 0 ? (
                            <ul>
                                {patientDetails.observations.map((observation, index) => (
                                    <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                        <strong>{observation.code}</strong>: {observation.value} <br />
                                    </div>
                                ))}
                            </ul>
                        ) : (
                            <p>No notes found.</p>
                        )}
                    </div>

                    <h4>Messages:</h4>
                    <div>
                        {messages && messages.length > 0 ? (
                            messages.map((message, index) => (
                                <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                    <strong>{message.senderFirstName}</strong> <p>{new Date(message.timeStamp).toLocaleString()}</p>
                                    <p>{message.message}</p>
                                </div>
                            ))
                        ) : (
                            <p>No messages available</p>
                        )}
                    </div>

                    <div className="message-section">
                        <h4>Send a Message:</h4>
                        <textarea
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            placeholder="Write your message here"
                        />
                        <button onClick={handleSendMessage} style={{ marginTop: "10px" }}>Send Message</button>
                    </div>
                </div>
            ) : (
                <div className="no-patient-details">
                    <p>No patient details found. Please log in and try again.</p>
                </div>
            )}
        </div>
    );


}

export default PatientDashboard;