import React from "react";

const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
};

const MessageList = ({ selectedPatient }) => {
    return (
        <div>
            <h4>Messages:</h4>
            <div>
                {selectedPatient.messages && selectedPatient.messages.length > 0 ? (
                    selectedPatient.messages.map((message, index) => (
                        <div key={index} style={{ padding: "10px", borderBottom: "1px solid #ccc" }}>
                            <div style={{ fontWeight: "bold" }}>
                                {message.sender.firstName} {message.sender.lastName}
                            </div>
                            <div style={{ fontStyle: "italic", color: "gray" }}>
                                Sent on: {formatDate(message.timeStamp)}
                            </div>
                            <div style={{ marginTop: "5px" }}>
                                <strong>Message:</strong>
                                <p>{message.message}</p>
                            </div>
                        </div>
                    ))
                ) : (
                    <p>No messages available</p>
                )}
            </div>
        </div>
    );
};

export default MessageList;
