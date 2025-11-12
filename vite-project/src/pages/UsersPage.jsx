import { useEffect, useState } from "react";
import _axios from "../axiosInstance.js";
import { Button, Table, Modal, Form } from "react-bootstrap";

const UsersPage = () => {
    const [users, setUsers] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingUser, setEditingUser] = useState(null);

    // Forma state
    const [id, setId] = useState(-1);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [type, setType] = useState("EVENT_CREATOR");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [active, setActive] = useState(true);

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const usersPerPage = 5;

    useEffect(() => {
        fetchAllUsers();
    }, []);

    const fetchAllUsers = async () => {
        try {
            const res = await _axios.get("/api/users/all");
            setUsers(res.data);
        } catch (e) {
            console.error("Greška pri dobijanju korisnika", e);
        }
    };

    const openModal = (user = null) => {
        if (user) {
            setEditingUser(user);
            setId(user.id);
            setFirstName(user.firstName);
            setLastName(user.lastName);
            setEmail(user.email);
            setType(user.type);
            setPassword("");
            setConfirmPassword("");
            setActive(user.active);
        } else {
            setEditingUser(null);
            setId(-1);
            setFirstName("");
            setLastName("");
            setEmail("");
            setType("EVENT_CREATOR");
            setPassword("");
            setConfirmPassword("");
            setActive(true);
        }
        setShowModal(true);
    };

    const handleSave = async () => {
        if (!editingUser && password !== confirmPassword) {
            alert("Lozinke se ne poklapaju!");
            return;
        }

        const payload = {
            id,
            firstName,
            lastName,
            email,
            type,
            password: password || undefined,
            active
        };

        try {
            if (editingUser) {
                await _axios.put(`/api/users/${editingUser.id}`, payload);
            } else {
                await _axios.post(`/api/users/add_user`, payload);
            }
            fetchAllUsers();
            setShowModal(false);
        } catch (err) {
            console.error(err);
            alert("Greška pri čuvanju korisnika!");
        }
    };

    const toggleActive = async (user) => {
        if (user.type === "ADMIN") {
            alert("Admin uvek ostaje aktivan!");
            return;
        }

        try {
            console.log("radimo toggle")
            await _axios.put(`/api/users/${user.id}/toggle`, {
                active: !user.active,
            });
            fetchAllUsers();
        } catch (err) {
            console.error(err);
            alert("Greška pri promeni statusa korisnika!");
        }
    };

    // Pagination logic
    const indexOfLast = currentPage * usersPerPage;
    const indexOfFirst = indexOfLast - usersPerPage;
    const currentUsers = users.slice(indexOfFirst, indexOfLast);
    const totalPages = Math.ceil(users.length / usersPerPage);

    return (
        <>
            <Button variant="success" className="mb-3" onClick={() => openModal()}>
                Dodaj novog korisnika
            </Button>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Ime</th>
                    <th>Prezime</th>
                    <th>Email</th>
                    <th>Tip</th>
                    <th>Status</th>
                    <th>Akcije</th>
                </tr>
                </thead>
                <tbody>
                {currentUsers.map((user) => (
                    <tr key={user.id}>
                        <td>{user.firstName}</td>
                        <td>{user.lastName}</td>
                        <td>{user.email}</td>
                        <td>{user.type}</td>
                        <td>{user.active ? "Aktivan" : "Neaktivan"}</td>
                        <td>
                            <Button
                                variant="primary"
                                size="sm"
                                className="me-2"
                                onClick={() => openModal(user)}
                            >
                                Izmeni
                            </Button>
                            <Button
                                variant={user.active ? "warning" : "success"}
                                size="sm"
                                onClick={() => toggleActive(user)}
                            >
                                {user.active ? "Deaktiviraj" : "Aktiviraj"}
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            {/* Pagination */}
            <div className="d-flex justify-content-center mb-3">
                {Array.from({ length: totalPages }, (_, i) => (
                    <Button
                        key={i}
                        variant={currentPage === i + 1 ? "primary" : "outline-primary"}
                        className="me-1"
                        onClick={() => setCurrentPage(i + 1)}
                    >
                        {i + 1}
                    </Button>
                ))}
            </div>

            {/* Modal za kreiranje/izmenu */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingUser ? "Izmeni korisnika" : "Dodaj novog korisnika"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-2">
                            <Form.Label>Ime</Form.Label>
                            <Form.Control
                                type="text"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                            />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Prezime</Form.Label>
                            <Form.Control
                                type="text"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                            />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Tip</Form.Label>
                            <Form.Select value={type} onChange={(e) => setType(e.target.value)}>
                                <option value="ADMIN">ADMIN</option>
                                <option value="EVENT_CREATOR">CREATOR</option>
                            </Form.Select>
                        </Form.Group>

                        {/* Lozinka samo ako se dodaje korisnik */}
                        {!editingUser && (
                            <>
                                <Form.Group className="mb-2">
                                    <Form.Label>Lozinka</Form.Label>
                                    <Form.Control
                                        type="password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-2">
                                    <Form.Label>Potvrdi lozinku</Form.Label>
                                    <Form.Control
                                        type="password"
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                    />
                                </Form.Group>
                            </>
                        )}
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Otkaži
                    </Button>
                    <Button variant="primary" onClick={handleSave}>
                        {editingUser ? "Sačuvaj izmene" : "Dodaj korisnika"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default UsersPage;
