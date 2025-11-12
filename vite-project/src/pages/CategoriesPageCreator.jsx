import { useEffect, useState } from "react";
import _axios from "../axiosInstance.js";
import { Card, Button, Modal, Form } from "react-bootstrap";

const CategoriesPageCreator = () => {
    const [allCategories, setAllCategories] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingCategory, setEditingCategory] = useState(null);
    const [formData, setFormData] = useState({ categoryName: "", categoryDescription: "" });
    const [error, setError] = useState("");

    // Fetch all categories
    const fetchCategories = async () => {
        try {
            const res = await _axios("/api/category/all");
            setAllCategories(res.data);
        } catch (e) {
            console.error("Greška prilikom dobijanja svih kategorija", e);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    // Open modal for adding or editing
    const openModal = (category = null) => {
        if (category) {
            setEditingCategory(category);
            setFormData({
                categoryName: category.categoryName,
                categoryDescription: category.categoryDescription,
            });
        } else {
            setEditingCategory(null);
            setFormData({ categoryName: "", categoryDescription: "" });
        }
        setError("");
        setShowModal(true);
    };

    // Handle form submit (add or edit)
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Provera duplikata
        const duplicate = allCategories.find(
            (cat) =>
                cat.categoryName.toLowerCase() === formData.categoryName.toLowerCase() &&
                (!editingCategory || cat.id !== editingCategory.id)
        );
        if (duplicate) {
            setError("Kategorija sa ovim nazivom već postoji!");
            return;
        }

        try {
            if (editingCategory) {
                // Update
                await _axios.put(`/api/category/${editingCategory.id}`, formData);
            } else {
                // Add
                await _axios.post("/api/category", formData);
            }
            setShowModal(false);
            fetchCategories();
        } catch (err) {
            console.error(err);
            setError("Greška prilikom čuvanja kategorije.");
        }
    };

    // Delete category
    const handleDelete = async (category) => {
        try {
            await _axios.delete(`/api/category/${category.id}`);
            fetchCategories();
        } catch (err) {
            console.error(err);
            alert("Ne možete obrisati kategoriju koja ima bar jedan događaj!");
        }
    };

    return (
        <div>
            <Button className="mb-3" onClick={() => openModal()}>Dodaj novu kategoriju</Button>

            <div className="d-flex flex-wrap gap-3">
                {allCategories.map((category) => (
                    <Card key={category.id} border="warning" style={{ width: "18rem" }}>
                        <Card.Header>{category.categoryName}</Card.Header>
                        <Card.Body>
                            <Card.Text>{category.categoryDescription}</Card.Text>
                            <Button variant="primary" className="me-2" onClick={() => openModal(category)}>
                                Izmeni
                            </Button>
                            <Button variant="danger" onClick={() => handleDelete(category)}>
                                Obriši
                            </Button>
                        </Card.Body>
                    </Card>
                ))}
            </div>

            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingCategory ? "Izmeni kategoriju" : "Dodaj novu kategoriju"}</Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleSubmit}>
                    <Modal.Body>
                        {error && <p style={{ color: "red" }}>{error}</p>}
                        <Form.Group className="mb-3">
                            <Form.Label>Naziv kategorije</Form.Label>
                            <Form.Control
                                type="text"
                                value={formData.categoryName}
                                onChange={(e) =>
                                    setFormData({ ...formData, categoryName: e.target.value })
                                }
                                required
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Opis kategorije</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                value={formData.categoryDescription}
                                onChange={(e) =>
                                    setFormData({ ...formData, categoryDescription: e.target.value })
                                }
                                required
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>Zatvori</Button>
                        <Button variant="success" type="submit">{editingCategory ? "Sačuvaj" : "Dodaj"}</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </div>
    );
};

export default CategoriesPageCreator;
