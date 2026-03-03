import React, { useState, useEffect, useContext } from "react";
import axios from "axios";
import { AuthContext } from "../context/AuthContext";
import Canvas from '../components/Canvas';
import '../App.css';


const AdminDashboard = () => {
    const { user } = useContext(AuthContext);
    const [patients, setPatients] = useState([]);
    const [selectedPatient, setSelectedPatient] = useState(null);
    const [newNote, setNewNote] = useState("");
    const [newDiagnosis, setNewDiagnosis] = useState("");
    const [newMessage, setNewMessage] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const authToken = sessionStorage.getItem("authToken");

    const [pageNumber, setPageNumber] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    const [practitionerInput, setPractitionerInput] = useState('');
    const [dateInput, setDateInput] = useState("");


    const [practitionerId, setPractitionerId] = useState(""); // För Practitioner ID
    const [date, setDate] = useState(""); // För datum

    const [encounters, setEncounters] = useState([]);
    const [loadingEncounters, setLoadingEncounters] = useState(false);
    const [errorEncounters, setErrorEncounters] = useState(null);


    //Search Portal
    const [name, setName] = useState('');
    const [patientId, setPatientId] = useState('');
    const [conditionCode, setConditionCode] = useState('');
    const [practitionerName, setPractitionerName] = useState('');
    const [searchCategory, setSearchCategory] = useState('name');
    const [patientsSearch, setPatientsSearch] = useState([]);
    const [loadingPatientQuarkus, setLoadingPatientQuarkus] = useState(true);
    const [errorPatientQuarkus, setErrorPatientQuarkus] = useState(null);

    //Image Upload
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [errorImage, setErrorImage] = useState(null);
    //Image edit
    const [imagePath, setImagePath] = useState(null);
    const [text, setText] = useState("");
    const [x, setX] = useState(0);
    const [y, setY] = useState(0);
    const [editedImagePath, setEditedImagePath] = useState(null);
    const [imageEditLoading, setImageEditLoading] = useState(false);
    const [imageEditError, setImageEditError] = useState(null);
    //Drawing (Canvas)
    const [imageDrawingPath, setImageDrawingPath] = useState(null);
    const [drawingData, setDrawingData] = useState(null);
    //Fetch Images
    const [images, setImages] = useState([]);
    const [fetchingImages, setFetchingImages] = useState(false);
    const [fetchImagesError, setFetchImagesError] = useState(null);

    useEffect(() => {
        setLoading(true);
        setError(null);

        axios
            .get(
                `https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/all/paginated?page=${pageNumber}&size=20`,
                {
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                    },
                }
            )
            .then((response) => {
                const { size, patients, totalPages, page, totalCount } = response.data;

                if (!patients || totalCount === undefined) {
                    console.error("Data saknas i responsen");
                    setError("Data saknas i responsen");
                    return;
                }

                setPatients(patients);
                setTotalPages(totalPages);
                setLoading(false);
            })
            .catch((err) => {
                setError("Ett fel inträffade vid hämtning av data: " + err.message);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [pageNumber]);

    useEffect(() => {
        const fetchImages = async () => {
            setFetchingImages(true);
            console.log('Fetching images from backend...');

            try {
                const response = await axios.get('https://fullstack-imageservice.app.cloud.cbh.kth.se/api/images');

                console.log('Response received:', response);
                console.log('Images data:', response.data);

                setImages(response.data);
                setFetchingImages(false);
                console.log('Images have been successfully loaded and set.');
            } catch (err) {
                setFetchImagesError('Failed to load images');
                setFetchingImages(false);
                console.error('Error fetching images:', err.message);
            }
        };
        fetchImages();
    }, []);

    if (fetchingImages) {
        return <p>Loading...</p>;
    }

    if (fetchImagesError) {
        return <p style={{ color: 'red' }}>{fetchImagesError}</p>;
    }

    const renderImages = (category) => {
        if (images[category] && images[category].length > 0) {
            return images[category].map((image, index) => (
                <img key={index} src={`https://fullstack-imageservice.app.cloud.cbh.kth.se${image}`} alt={`${category} image`} />
            ));
        } else {
            return <p>No images in {category}</p>;
        }
    };

    const handleDrawingUpload = async (e) => {
        const formData = new FormData();
        formData.append('image', e.target.files[0]);

        try {
            const response = await axios.post('https://fullstack-imageservice.app.cloud.cbh.kth.se/api/images/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            setImageDrawingPath(response.data.filePath);
        } catch (err) {
            console.error('Upload failed:', err);
        }
    };

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
        setImageEditError(null);
    };


    const handleUpload = async () => {
        if (!selectedFile) {
            setImageEditError("Please select a file to upload.");
            return;
        }

        const formData = new FormData();
        formData.append("image", selectedFile);

        try {
            const response = await axios.post("https://fullstack-imageservice.app.cloud.cbh.kth.se/api/images/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            console.log("Upload response:", response.data);

            if (response.data && response.data.filePath) {
                const uploadedImagePath = `https://fullstack-imageservice.app.cloud.cbh.kth.se${response.data.filePath}`;
                setImagePath(uploadedImagePath);
            } else {
                setImageEditError("Failed to get image path from server.");
            }
        } catch (err) {
            setImageEditError("Failed to upload image.");
            console.error("Upload error:", err);
        }
    };

    const handleDrawOnImage = async () => {
        if (!imagePath) {
            setImageEditError("No image uploaded to draw on.");
            return;
        }

        if (!drawingData) {
            setImageEditError("No drawing data available.");
            return;
        }

        setImageEditLoading(true);

        try {
            const response = await axios.post("https://fullstack-imageservice.app.cloud.cbh.kth.se/api/images/draw", {
                filePath: imagePath.replace("https://fullstack-imageservice.app.cloud.cbh.kth.se", ""),
                drawingData,
            });

            setImageEditLoading(false);
            setImageDrawingPath(`https://fullstack-imageservice.app.cloud.cbh.kth.se${response.data.drawnFilePath}`);
            setImageEditError(null);

            console.log("Received drawOnImage request");
            console.log("File path:", filePath);
            console.log("Drawing data length:", drawingData ? drawingData.length : "No data");

        } catch (err) {
            setImageEditLoading(false);
            setImageEditError("Failed to draw on image.");
        }
    };

    const handleEdit = async () => {
        if (!text) {
            setImageEditError("Please enter some text.");
            return;
        }

        setImageEditLoading(true);

        try {
            console.log("Original filePath:", imagePath);

            const relativeFilePath = imagePath.replace("https://fullstack-imageservice.app.cloud.cbh.kth.se", "");
            console.log("Relative filePath to send to server:", relativeFilePath);

            const response = await axios.post("https://fullstack-imageservice.app.cloud.cbh.kth.se/api/images/edit", {
                filePath: relativeFilePath,
                text,
                x,
                y,
            });

            setImageEditLoading(false);

            setEditedImagePath(`https://fullstack-imageservice.app.cloud.cbh.kth.se${response.data.editedFilePath}`);
            setImageEditError(null);

            console.log("Original Image Path:", imagePath);
            console.log("Edited Image Path:", response.data.editedFilePath);

        } catch (err) {
            setImageEditLoading(false);
            setImageEditError("Failed to edit image.");
            console.error("Error occurred:", err);
        }
    };


    const handleSearchPractitionerByName = (practitionerName) => {
        setLoadingPatientQuarkus(true);
        setErrorPatientQuarkus(null);

        if (!practitionerName) {
            console.error("Practitioner-namn måste anges.");
            setErrorPatientQuarkus("Practitioner-namn måste anges.");
            setLoadingPatientQuarkus(false);
            return;
        }

        const params = { name: practitionerName };

        axios
            .get('https://fullstack-searchservice.app.cloud.cbh.kth.se/practitioners/search', { params })
            .then((response) => {
                const { data } = response;

                if (!data || data.resourceType !== 'Bundle' || !data.entry) {
                    console.error("Inga practitioner-resurser hittades för det angivna namnet.");
                    setErrorPatientQuarkus("Inga practitioner-resurser hittades för det angivna namnet.");
                    return;
                }

                setPatientsSearch(data.entry.map((entry) => entry.resource));
                setLoadingPatientQuarkus(false);
            })
            .catch((err) => {
                console.error("Fel vid hämtning av data:", err.message);
                setErrorPatientQuarkus("Ett fel inträffade vid hämtning av data: " + err.message);
            })
            .finally(() => {
                setLoadingPatientQuarkus(false);
            });
    };

    const handlePatientSearchByCondition = (patientId, conditionCode) => {
        setLoadingPatientQuarkus(true);
        setErrorPatientQuarkus(null);

        if (!patientId || !conditionCode) {
            console.error("Patient-ID och condition-kod måste anges.");
            setErrorPatientQuarkus("Patient-ID och condition-kod måste anges.");
            setLoadingPatientQuarkus(false);
            return;
        }

        const params = { patientId, conditionCode };

        axios
            .get('https://fullstack-searchservice.app.cloud.cbh.kth.se/patients/search-by-condition', { params })
            .then((response) => {
                const { data } = response;

                console.log("Full response:", response);
                console.log("Response data:", data);

                if (!data || data.resourceType !== 'Bundle' || !data.entry) {
                    console.error("Inga condition-resurser hittades för patienten.");
                    setErrorPatientQuarkus("Inga condition-resurser hittades för patienten.");
                    return;
                }

                setPatientsSearch(data.entry.map((entry) => entry.resource));
                setLoadingPatientQuarkus(false);
            })
            .catch((err) => {
                console.error("Fel vid hämtning av data:", err.message);
                setErrorPatientQuarkus("Ett fel inträffade vid hämtning av data: " + err.message);
            })
            .finally(() => {
                setLoadingPatientQuarkus(false);
            });
    };

    const handlePatientSearchQuarkus = (query, category) => {
        setLoadingPatientQuarkus(true);
        setErrorPatientQuarkus(null);

        const params = category === 'name' ? { name: query } : { condition: query };

        axios
            .get('https://fullstack-searchservice.app.cloud.cbh.kth.se/patients/search', { params })
            .then((response) => {

                const { data } = response;
                console.log("Full response:", response);
                console.log("Response data:", data);


                if (!data || !data.entry) {
                    console.error("Data saknas i responsen");
                    setErrorPatientQuarkus("Data saknas i responsen");
                    return;
                }

                setPatientsSearch(data.entry);
                setLoadingPatientQuarkus(false);
            })
            .catch((err) => {
                console.error("Fel vid hämtning av data:", err.message);
                setErrorPatientQuarkus("Ett fel inträffade vid hämtning av data: " + err.message);
            })
            .finally(() => {
                setLoadingPatientQuarkus(false);
            });
    };


    const renderPatientDetails = (patient) => {
        const { resource } = patient;

        const name = resource?.name && resource.name[0]?.given && resource.name[0]?.family
            ? `${resource.name[0].given.join(" ")} ${resource.name[0].family}`
            : null;

        if (!name) {
            return null;
        }

        const id = resource?.id || "ID saknas";
        const personnummer = resource?.identifier ? resource.identifier.find(i => i.type?.coding?.some(c => c.code === "PN"))?.value : "Personnummer saknas";
        const birthDate = resource?.birthDate || "Födelsedatum saknas";
        const address = resource?.address ? `${resource.address[0].line.join(" ")} ${resource.address[0].city}, ${resource.address[0].country}` : "Adress saknas";

        return (
            <div key={id} className="patient-card" style={{ border: "1px solid #ddd", margin: "10px", padding: "10px" }}>
                <h3>{name}</h3>
                <p><strong>ID:</strong> {id}</p>
                <p><strong>Personnummer:</strong> {personnummer}</p>
                <p><strong>Födelsedatum:</strong> {birthDate}</p>
                <p><strong>Adress:</strong> {address}</p>
            </div>
        );
    };

    const renderPractitionerDetails = (practitioner) => {
        if (!practitioner) {
            return <p>Ingen Practitioner-data tillgänglig</p>;
        }

        const id = practitioner.id || "Id ej tillgängligt";
        const name = practitioner.name?.[0];
        const fullName = name
            ? `${name.given?.join(" ")} ${name.family}`
            : "Namn ej tillgängligt";

        const identifier = practitioner.identifier?.[0]?.value || "Ingen identifierare";
        const email = practitioner.telecom?.find((t) => t.system === "email")?.value || "Ingen e-post";
        const address = practitioner.address?.[0];
        const addressLines = address?.line?.join(", ") || "Ingen adress";
        const city = address?.city || "Stad ej tillgänglig";
        const state = address?.state || "Län ej tillgänglig";
        const postalCode = address?.postalCode || "Postnummer ej tillgängligt";
        const country = address?.country || "Land ej tillgängligt";
        const gender = practitioner.gender || "Kön ej specificerat";

        return (
            <div style={{ border: "1px solid #ccc", padding: "10px", marginBottom: "10px", borderRadius: "5px" }}>
                <h3>Practitioner Information</h3>
                <p><strong>Id:</strong> {id}</p>
                <p><strong>Namn:</strong> {fullName}</p>
                <p><strong>Identifierare:</strong> {identifier}</p>
                <p><strong>E-post:</strong> {email}</p>
                <h4>Adress:</h4>
                <p>{addressLines}</p>
                <p>{city}, {state}, {postalCode}</p>
                <p>{country}</p>
                <p><strong>Kön:</strong> {gender}</p>
            </div>
        );
    };


    const handleSearchEncounters = (practitioner, date) => {
        console.log("Practitioner ID:", practitioner);
        console.log("Date:", date);

        if (!practitioner || !date) {
            alert("Please enter both practitioner ID and date.");
            return;
        }

        setLoadingEncounters(true);
        setErrorEncounters(null);

        axios
            .get('https://fullstack-searchservice.app.cloud.cbh.kth.se/practitioners/encounters', {
                params: {
                    date: date,
                    practitioner: practitioner
                }
            })
            .then((response) => {
                const { data } = response;
                console.log("Full response:", response);
                console.log("Response data:", data);

                if (!data || !data.entry) {
                    console.error("Inga encounters hittades för den angivna praktikern och datumet.");
                    setErrorEncounters("Inga encounters hittades för den angivna praktikern och datumet.");
                    return;
                }

                setPatientsSearch(data.entry.map((entry) => entry.resource));
                console.log("Patients Search Data:", patientsSearch);
                console.log("Encounter entries:", data.entry);
            })
            .catch((err) => {
                console.error("Fel vid hämtning av data:", err.message);
                setErrorEncounters("Ett fel inträffade vid hämtning av data: " + err.message);
            })
            .finally(() => {
                setLoadingEncounters(false);
            });
    };


    const renderEncounterDetails = (encounter) => {
        console.log("Encounter:", encounter);

        if (!encounter) {
            return <p>Ingen Encounter-data tillgänglig</p>;
        }

        const id = encounter.id || "Id ej tillgängligt";
        const status = encounter.status || "Status ej tillgänglig";
        const encounterClass = encounter.class?.code || "Klass ej tillgänglig";
        const encounterType = encounter.type?.[0]?.text || "Typ ej tillgänglig";
        const date = encounter.period?.start ? new Date(encounter.period.start).toLocaleString() : "Datum ej tillgängligt";
        const practitioner = encounter.participant?.find((p) => p.individual)?.individual;
        const practitionerName = practitioner ? practitioner.display : "Praktiker ej tillgänglig";
        const patientReference = encounter.subject?.reference || "Patient ej tillgänglig";
        const serviceProvider = encounter.serviceProvider?.display || "Serviceprovider ej tillgänglig";

        return (
            <div style={{ border: "1px solid #ccc", padding: "10px", marginBottom: "10px", borderRadius: "5px" }}>
                <h3>Encounter Information</h3>
                <p><strong>Id:</strong> {id}</p>
                <p><strong>Status:</strong> {status}</p>
                <p><strong>Class:</strong> {encounterClass}</p>
                <p><strong>Typ:</strong> {encounterType}</p>
                <p><strong>Datum:</strong> {date}</p>
                <p><strong>Praktiker:</strong> {practitionerName}</p>
                <p><strong>Patient:</strong> {patientReference}</p>
                <p><strong>Serviceprovider:</strong> {serviceProvider}</p>
            </div>
        );
    };



    const handlePreviousPage = () => {
        if (pageNumber > 1) {
            setPageNumber(pageNumber - 1);
        }
    };

    const handleNextPage = () => {
        if (pageNumber < totalPages) {
            setPageNumber(pageNumber + 1);
        }
    };

    const handleSelectPatient = (patient) => {
        setSelectedPatient(null);
        setLoading(true);
        console.log("Fetching details for patient:", patient.id);

        axios
            .get(`https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/${patient.id}/details`,
                {
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                    },
                }
                )
            .then((response) => {
                const patientData = response.data;
                if (!Array.isArray(patientData.observations)) {
                    console.log("observations is not an array. Setting it to an empty array.");
                    patientData.observations = [];
                }
                if (!Array.isArray(patientData.conditions)) {
                    console.log("conditions is not an array. Setting it to an empty array.");
                    patientData.conditions = [];
                }

                console.log("Personnummer from backend: ", patientData.personnummer);
                setSelectedPatient(patientData);

                return axios.post(`https://fullstack-messageservice.app.cloud.cbh.kth.se/messages/getMessages`, null, {
                    params:
                        {
                            patientPersonnummer: patientData.personnummer
                        },
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                    },
                });
            })

                .then((response) => {
                const messages = response.data;

                const updatedMessages = messages.map((message) => ({
                    ...message,
                    senderFirstName: message.sender.firstName,
                }));

                setSelectedPatient((prevPatient) => ({
                    ...prevPatient,
                    messages: updatedMessages,
                }));

                console.log("Messages fetched successfully:", messages);
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


    const safePatients = Array.isArray(patients) ? patients : [];

    const generateUniqueId = () => {
        return 'xxxxxx'.replace(/[x]/g, () => (Math.random() * 16 | 0).toString(16));
    };

    const handleAddNote = () => {
        if (newNote.trim() === "") {
            alert("Note cannot be empty");
            return;
        }
        console.log("Adding note:", newNote);

        const newObservation = {
            id: generateUniqueId(),
            code: `${user.firstName} ${user.lastName}`,
            value: newNote,
            date: new Date().toISOString(),
        };

        axios.post(`https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/${selectedPatient.id}/addNote`, newObservation, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${authToken}`,
            }
        })
            .then(() => {
                console.log("Note added successfully to the server.");

                const updatedPatient = {
                    ...selectedPatient,
                    observations: Array.isArray(selectedPatient.observations)
                        ? [...selectedPatient.observations, newObservation]
                        : [newObservation],
                };
                console.log("Updated patient data with new note:", updatedPatient);

                setSelectedPatient(updatedPatient);
                setNewNote("");
            })
            .catch((error) => {
                console.error("Error adding note:", error);
                alert("Failed to add note: " + error.message);
            });
    };

    const handleAddDiagnosis = () => {
        if (newDiagnosis.trim() === "") {
            alert("Diagnosis cannot be empty");
            return;
        }

        console.log("Adding diagnosis:", newDiagnosis);

        const newCondition = {
            id: generateUniqueId(),
            code: newDiagnosis,
            description: newDiagnosis,
            onsetDate: new Date().toISOString(),
        };

        axios.post(`https://fullstack-fhirservice.app.cloud.cbh.kth.se/api/patient/${selectedPatient.id}/addDiagnosis`, newCondition, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${authToken}`,
            },
        })
                .then(() => {
                console.log("Diagnosis added successfully to the server.");

                const updatedPatient = {
                    ...selectedPatient,
                    conditions: Array.isArray(selectedPatient.conditions)
                        ? [...selectedPatient.conditions, newCondition]
                        : [newCondition],
                };
                setSelectedPatient(updatedPatient);
                setNewDiagnosis("");
            })
            .catch((error) => {
                console.error("Error adding diagnosis:", error);
                alert("Failed to add diagnosis: " + error.message);
            });
    };


    const handleSendMessage = () => {
        if (newMessage.trim() === "") {
            alert("Message cannot be empty");
            return;
        }

        const currentDateTime = new Date().toISOString();
        const senderPersonnummer = user.personnummer;
        const receiverPersonnummer = selectedPatient.personnummer;

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
                    ...selectedPatient,
                    messages: [
                        ...(selectedPatient.messages || []),
                        {
                            from: user.name,      // Skicka avsändarens namn
                            content: newMessage,  // Skicka meddelandet
                            timeStamp: currentDateTime,
                        }
                    ],
                };
                setSelectedPatient(updatedPatient);
                setNewMessage("");
            })
            .catch((error) => {
                console.error("Error sending message:", error);
                alert("Failed to send message: " + error.message);
            });
    };

    const renderObservationValue = (observation) => {
        if (observation.value && observation.value instanceof Object) {
            return `${observation.value.value} ${observation.value.unit || ''}`;
        } else {
            return observation.value || "No value";
        }
    };

    return (
        <div>
            <h2>Welcome! This table is only visible for admins!</h2>

            <div style={{ display: "flex", justifyContent: "space-between", marginTop: "10px" }}>
                <div style={{ width: "1000%" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "20px" }}>
                        <button onClick={handlePreviousPage} disabled={pageNumber === 1}>
                            Previous
                        </button>
                        <span>Page {pageNumber} of {totalPages}</span>
                        <button onClick={handleNextPage} disabled={pageNumber === totalPages}>
                            Next
                        </button>
                    </div>

                    {loading && <div>Loading patients...</div>}
                    {error && <div>Error: {error}</div>}

                    <div style={{ display: "flex" }}>
                        <div style={{ width: "50%", borderRight: "1px solid #ccc", paddingRight: "10px" }}>
                            <h3>Patient List</h3>
                            <ul style={{ listStyleType: "none", paddingLeft: "0" }}>
                                {safePatients.length === 0 ? (
                                    <li>No patients found</li>
                                ) : (
                                    safePatients.map((patient) => (
                                        <li
                                            key={patient.id}
                                            onClick={() => handleSelectPatient(patient)}
                                            style={{
                                                cursor: "pointer",
                                                padding: "10px",
                                                borderBottom: "1px solid #eee",
                                            }}
                                        >
                                            {patient.firstName} {patient.lastName} - {calculateAge(patient.birthDate)}
                                        </li>
                                    ))
                                )}
                            </ul>
                        </div>

                        <div style={{ width: "70%", paddingLeft: "20px" }}>
                            {selectedPatient ? (
                                <div>
                                    <h3>Details for {selectedPatient.firstName} {selectedPatient.lastName}</h3>
                                    <p><strong>Personnummer:</strong> {selectedPatient.personnummer}</p>
                                    <p><strong>Gender:</strong> {selectedPatient.gender}</p>
                                    <p><strong>Age:</strong> {calculateAge(selectedPatient.birthDate)}</p>
                                    <h4>Diagnosis:</h4>
                                    <div>
                                        {selectedPatient.conditions && selectedPatient.conditions.length > 0 ? (
                                            selectedPatient.conditions.map((condition, index) => (
                                                <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                                    <p><strong>Description:</strong> {condition.description}</p>
                                                    {condition.onsetDate && (
                                                        <p><strong>Onset Date:</strong> {new Date(condition.onsetDate).toLocaleDateString()}</p>
                                                    )}
                                                </div>
                                            ))
                                        ) : (
                                            <p>No diagnoses available</p>
                                        )}
                                    </div>

                                    <h4>Messages:</h4>
                                    <div>
                                        {selectedPatient.messages && selectedPatient.messages.length > 0 ? (
                                            selectedPatient.messages.map((message, index) => (
                                                <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                                    <strong>{message.senderFirstName} </strong> <p>{message.timeStamp}</p>
                                                    <p>{message.message}</p>
                                                </div>
                                            ))
                                        ) : (
                                            <p>No messages available</p>
                                        )}
                                    </div>

                                    <h4>Notes:</h4>
                                    <div>
                                        {selectedPatient.observations && selectedPatient.observations.length > 0 ? (
                                            selectedPatient.observations.map((observation, index) => (
                                                <div key={index} style={{ padding: "5px", borderBottom: "1px solid #ccc" }}>
                                                    <strong>{observation.code}:</strong>
                                                    <p>{renderObservationValue(observation)}</p>
                                                </div>
                                            ))
                                        ) : (
                                            <p>No notes available</p>
                                        )}
                                    </div>

                                    <div>
                                        <h4>Add Note</h4>
                                        <textarea
                                            value={newNote}
                                            onChange={(e) => setNewNote(e.target.value)}
                                            rows="3"
                                            style={{ width: "100%" }}
                                            placeholder="Write a note..."
                                        />
                                        <button onClick={handleAddNote} style={{ marginTop: "10px" }}>Add Note</button>
                                    </div>

                                    <div style={{ marginTop: "20px" }}>
                                        <h4>Add Diagnosis</h4>
                                        <input
                                            type="text"
                                            value={newDiagnosis}
                                            onChange={(e) => setNewDiagnosis(e.target.value)}
                                            placeholder="Enter new diagnosis"
                                            style={{ width: "100%", padding: "8px" }}
                                        />
                                        <button onClick={handleAddDiagnosis} style={{ marginTop: "10px" }}>Add Diagnosis</button>
                                    </div>

                                    <div style={{ marginTop: "20px" }}>
                                        <h4>Send Message</h4>
                                        <textarea
                                            value={newMessage}
                                            onChange={(e) => setNewMessage(e.target.value)}
                                            rows="3"
                                            style={{ width: "100%" }}
                                            placeholder="Write a message..."
                                        />
                                        <button onClick={handleSendMessage} style={{ marginTop: "10px" }}>Send Message</button>
                                    </div>
                                </div>
                            ) : (
                                <p>Select a patient to view details</p>
                            )}
                        </div>
                    </div>
                </div>

                <div style={{ width: "80%", paddingLeft: "20px", marginTop: "20px" }}>
                    <h4>Search by Practitioner and Date</h4>
                    <div style={{ marginBottom: "20px" }}>
                        <div style={{ display: "flex", flexDirection: "column" }}>
                            <label>Läkare:</label>
                            <input
                                type="text"
                                value={practitionerInput}
                                onChange={(e) => setPractitionerInput(e.target.value)}
                                placeholder="Enter doctor's name"
                                style={{ width: "100%", marginBottom: "10px" }}
                            />
                            <label>Datum:</label>
                            <input
                                type="date"
                                value={dateInput}
                                onChange={(e) => setDateInput(e.target.value)}
                                style={{ width: "100%", marginBottom: "10px" }}
                            />
                            <button
                                onClick={() => handleSearchEncounters(practitionerInput, dateInput)}
                                style={{ marginTop: "10px" }}
                            >
                                Search
                            </button>
                        </div>

                        <div>
                            {searchCategory === "encounters" && patientsSearch && patientsSearch.length > 0 ? (
                                patientsSearch.map((encounter) => renderEncounterDetails(encounter))
                            ) : (
                                <p>Inga encounters funna eller fel vid hämtning</p>
                            )}
                        </div>

                    </div>
                </div>

                <div style={{ width: "200%", paddingLeft: "20px", marginTop: "20px" }}>
                    <h2>Search Portal!</h2>

                    <select
                        value={searchCategory}
                        onChange={(e) => setSearchCategory(e.target.value)}
                        style={{ padding: "10px", marginRight: "10px" }}
                    >
                        <option value="name">Search by Patient Name</option>
                        <option value="condition">Search by Patient Id and Condition Code</option>
                        <option value="practitionerName">Search by Practitioner Name</option>
                        <option value="encounters">Search by Practitioner Encounters</option>
                    </select>

                    {searchCategory === "name" ? (
                        <input
                            type="text"
                            placeholder="Search by Patient Name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            style={{ padding: "10px", width: "80%", marginBottom: "20px" }}
                        />
                    ) : searchCategory === "condition" ? (
                        <div>
                            <input
                                type="text"
                                placeholder="Patient ID"
                                value={patientId}
                                onChange={(e) => setPatientId(e.target.value)}
                                style={{ padding: "10px", width: "40%", marginBottom: "10px", marginRight: "10px" }}
                            />
                            <input
                                type="text"
                                placeholder="Condition Code"
                                value={conditionCode}
                                onChange={(e) => setConditionCode(e.target.value)}
                                style={{ padding: "10px", width: "40%", marginBottom: "10px" }}
                            />
                        </div>
                    ) : searchCategory === "practitionerName" ? (
                        <input
                            type="text"
                            placeholder="Practitioner Name"
                            value={practitionerName}
                            onChange={(e) => setPractitionerName(e.target.value)}
                            style={{ padding: "10px", width: "80%", marginBottom: "20px" }}
                        />
                    ) : searchCategory === "encounters" ? (
                        <div>
                            <input
                                type="text"
                                placeholder="Practitioner ID"
                                value={practitionerId}
                                onChange={(e) => setPractitionerId(e.target.value)}
                                style={{ padding: "10px", width: "40%", marginBottom: "10px", marginRight: "10px" }}
                            />
                            <input
                                type="date"
                                placeholder="Date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                                style={{ padding: "10px", width: "40%", marginBottom: "10px" }}
                            />
                        </div>
                    ) : null}

                    <button
                        onClick={() => {
                            if (searchCategory === "name") {
                                handlePatientSearchQuarkus(name, searchCategory);
                            } else if (searchCategory === "condition") {
                                handlePatientSearchByCondition(patientId, conditionCode);
                            } else if (searchCategory === "practitionerName") {
                                handleSearchPractitionerByName(practitionerName);
                            } else if (searchCategory === "encounters") {
                                handleSearchEncounters(practitionerId, date);
                            }
                        }}
                        style={{
                            padding: "10px 20px",
                            fontSize: "16px",
                            cursor: "pointer",
                            backgroundColor: "#4CAF50",
                            color: "white",
                            border: "none",
                            borderRadius: "5px",
                        }}
                    >
                        Search
                    </button>

                    {loadingPatientQuarkus && <p>Laddar...</p>}
                    {errorPatientQuarkus && <p style={{ color: "red" }}>{errorPatientQuarkus}</p>}

                    <div>
                        {searchCategory === "practitionerName" && patientsSearch.length > 0 ? (
                            patientsSearch.map((practitioner) => renderPractitionerDetails(practitioner))
                        ) : searchCategory !== "practitionerName" && patientsSearch.length > 0 ? (
                            patientsSearch.map((patient) => renderPatientDetails(patient))
                        ) : searchCategory === "encounters" && patientsSearch.length > 0 ? (
                            patientsSearch.map((encounter) => renderEncounterDetails(encounter))
                        ) : (
                            !loadingPatientQuarkus && <p>Inga resurser funna</p>
                        )}
                    </div>
                </div>

                <div>
                    <h3>Image Upload and Edit</h3>

                    <div style={{ marginBottom: "20px" }}>
                        <h4>Upload an Image here!</h4>
                        <input type="file" accept="image/*" onChange={handleFileChange} />
                        <button onClick={handleUpload}>Upload</button>
                    </div>

                    <div>
                    {imagePath && (
                        <div>
                            <h4>Uploaded Image:</h4>
                            <img
                                src={imagePath}
                                alt="Uploaded"
                                style={{ maxWidth: "100%", marginBottom: "10px" }}
                            />
                        </div>
                    )}
                </div>
                    {imagePath && (
                        <div>
                            <h4>Edit Image</h4>
                            <input
                                type="text"
                                placeholder="Enter text"
                                value={text}
                                onChange={(e) => setText(e.target.value)}
                                style={{ marginRight: "10px" }}
                            />
                            <input
                                type="number"
                                placeholder="X Coordinate"
                                value={x}
                                onChange={(e) => setX(e.target.value)}
                                style={{ marginRight: "10px" }}
                            />
                            <input
                                type="number"
                                placeholder="Y Coordinate"
                                value={y}
                                onChange={(e) => setY(e.target.value)}
                            />
                            <button onClick={handleEdit} disabled={imageEditLoading}>
                                {imageEditLoading ? "Editing..." : "Edit"}
                            </button>
                        </div>
                    )}

                    {editedImagePath && (
                        <div>
                            <h4>Edited Image:</h4>
                            <img
                                src={editedImagePath}
                                alt="Edited"
                                style={{ maxWidth: "100%" }}
                            />
                        </div>
                    )}

                    {imageEditError && <p style={{ color: "red" }}>{imageEditError}</p>}
                </div>

                {imagePath && (
                    <div>
                        <h4>Draw on Image</h4>
                        <Canvas
                            imagePath={imagePath}
                            onSave={(data) => setDrawingData(data)}
                        />
                        <button onClick={handleDrawOnImage} disabled={!drawingData}>
                            Save Drawing
                        </button>
                    </div>
                )}

                <div>
                    <h3>Images from All Directories</h3>

                    {images.uploads && images.uploads.map((image, index) => (
                        <img key={index} src={`https://fullstack-imageservice.app.cloud.cbh.kth.se${image}`} alt={`Uploaded ${index}`} className="image-style" />
                    ))}

                    {images.drawn && images.drawn.map((image, index) => (
                        <img key={index} src={`https://fullstack-imageservice.app.cloud.cbh.kth.se${image}`} alt={`Drawn ${index}`} className="image-style" />
                    ))}

                    {images.edited && images.edited.map((image, index) => (
                        <img key={index} src={`https://fullstack-imageservice.app.cloud.cbh.kth.se${image}`} alt={`Edited ${index}`} className="image-style" />
                    ))}

                </div>
            </div>

            <div style={{ display: "flex", justifyContent: "space-between", marginTop: "10px" }}>
                <button onClick={handlePreviousPage} disabled={pageNumber === 1}>
                    Previous
                </button>
                <span>Page {pageNumber} of {totalPages}</span>
                <button onClick={handleNextPage} disabled={pageNumber === totalPages}>
                    Next
                </button>
            </div>
        </div>
    );

};

export default AdminDashboard;
