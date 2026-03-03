export const patients = [
    {
        id: 1,
        name: "John Doe",
        age: 35,
        gender: "Male",
        diagnosis: "Flu",
        notes: ["Needs rest hydration."],
        messages: [
            { from: "doctor", content: "Please take your medications as prescribed." }
        ]
    },
    {
        id: 2,
        name: "Jane Smith",
        age: 28,
        gender: "Female",
        diagnosis: "Migraine",
        notes: ["Prescribed painkillers"],
        messages: [
            { from: "staff", content: "Remember to follow up in two weeks." }
        ]
    },
    {
        id: 3,
        name: "Emily Johnson",
        age: 42,
        gender: "Female",
        diagnosis: "Diabetes",
        notes: ["Monitor blood sugar levels regularly"],
        messages: [
            { from: "doctor", content: "Keep track of your diet and exercise regularly." }
        ]
    }
];

export const users = [
    {
        id: 1,
        role: "patient",
        name: "John Doe",
        username: "patient",
        password: "patient"
    },
    {
        id: 2,
        role: "doctor",
        name: "Dr. Sarah",
        username: "doctor",
        password: "doctor"
    },
    {
        id: 3,
        role: "staff",
        name: "Staff Member",
        username: "staff",
        password: "staff"
    }
];