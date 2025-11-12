import { useEffect, useState } from "react";
import _axios from "../axiosInstance.js";
import { Button, Table, Modal, Form } from "react-bootstrap";

const EventsCreatorPage = () => {
    const [events, setEvents] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingEvent, setEditingEvent] = useState(null);

    const [id, setId] = useState(-1);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [eventDate, setEventDate] = useState("");
    const [location, setLocation] = useState("");
    const [category, setCategory] = useState("");
    const [tags, setTags] = useState("");
    const [maxCapacity, setMaxCapacity] = useState("");

    const [categories, setCategories] = useState([]);

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const eventsPerPage = 5;

    //const navigate = useNavigate();

    useEffect(() => {
        fetchAllEvents();
        fetchCategories();
    }, []);

    const fetchAllEvents = async () => {
        try {
            const res = await _axios("/api/events/all");
            const sorted = res.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            setEvents(sorted);
        } catch (e) {
            console.error("Nismo uspeli da dobijemo eventove", e);
        }
    };

    const fetchCategories = async () => {
        try {
            const res = await _axios("/api/category/all");
            setCategories(res.data);
        } catch (e) {
            console.error("Greška pri dobijanju kategorija", e);
        }
    };

    const handleDelete = async (event) => {
        if (!window.confirm("Da li ste sigurni da želite da obrišete ovaj događaj?")) return;
        try {
            console.log(event.id)
            await _axios.delete(`/api/events/${event.id}`);
            fetchAllEvents();
        } catch (err) {
            console.error(err);
            alert("Ne mozete obrisati event!");
        }
    };

    const openModal = (event = null) => {
        if (event) {
            setEditingEvent(event);
            setId(event.id);
            setTitle(event.title);
            setDescription(event.description);
            setEventDate(event.dateTime);
            setLocation(event.location);
            setCategory(event.categoryName);
            setTags(Array.isArray(event.tags) ? event.tags.join(", ") : event.tags);
            setMaxCapacity(event.maxCapacity || "");
        } else {
            setEditingEvent(null);
            setId(-1);
            setTitle("");
            setDescription("");
            setEventDate("");
            setLocation("");
            setCategory("");
            setTags("");
            setMaxCapacity("");
        }
        setShowModal(true);
    };

    const handleSave = async () => {
        const normalizedDate = eventDate.length === 16 ? eventDate + ":00" : eventDate;

        const payload = {
            id,
            title,
            description,
            eventDate: normalizedDate,
            location,
            category: category,
            tags: tags,
            capacity: maxCapacity ? parseInt(maxCapacity) : null
        };

        try {
            if (editingEvent) {
                console.log("Menjamo event")
                await _axios.put(`/api/events/${editingEvent.id}`, payload);
            } else {
                console.log("dodajemo novi event")
                await _axios.post(`/api/events/add_event`, payload);
            }
            fetchAllEvents();
            setShowModal(false);
        } catch (err) {
            console.error(err);
            alert("Greška pri čuvanju događaja!");
        }
    };

    const openEventDetail = (id) => {
        localStorage.setItem("eventDetail", id.toString())
        //navigate('/')
    }


    // Pagination logic
    const indexOfLast = currentPage * eventsPerPage;
    const indexOfFirst = indexOfLast - eventsPerPage;
    const currentEvents = events.slice(indexOfFirst, indexOfLast);
    const totalPages = Math.ceil(events.length / eventsPerPage);

    return (
        <>
            <Button variant="success" className="mb-3" onClick={() => openModal()}>Dodaj novi događaj</Button>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Naslov</th>
                    <th>Autor</th>
                    <th>Datum kreiranja</th>
                    <th>Akcije</th>
                </tr>
                </thead>
                <tbody>
                {currentEvents.map((event) => (
                    <tr key={event.id}>
                        <td onClick={()=> openEventDetail(event.id)}>
                            <a href={`/`} target="_blank" rel="noopener noreferrer">
                                {event.title}
                            </a>
                        </td>
                        <td>{event.authorName}</td>
                        <td>{new Date(event.createdAt).toLocaleString()}</td>
                        <td>
                            <Button variant="primary" size="sm" className="me-2" onClick={() => openModal(event)}>Izmeni</Button>
                            <Button variant="danger" size="sm" onClick={() => handleDelete(event)}>Obriši</Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            {/* Pagination */}
            <div className="d-flex justify-content-center mb-3">
                {Array.from({ length: totalPages }, (_, i) => (
                    <Button key={i} variant={currentPage === i + 1 ? "primary" : "outline-primary"} className="me-1" onClick={() => setCurrentPage(i + 1)}>
                        {i + 1}
                    </Button>
                ))}
            </div>

            {/* Modal za kreiranje/izmenu */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingEvent ? "Izmeni događaj" : "Dodaj novi događaj"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-2">
                            <Form.Label>Naslov</Form.Label>
                            <Form.Control type="text" value={title} onChange={(e) => setTitle(e.target.value)} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Opis</Form.Label>
                            <Form.Control as="textarea" value={description} onChange={(e) => setDescription(e.target.value)} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Datum i vreme održavanja</Form.Label>
                            <Form.Control type="datetime-local" value={eventDate} onChange={(e) => setEventDate(e.target.value)} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Lokacija</Form.Label>
                            <Form.Control type="text" value={location} onChange={(e) => setLocation(e.target.value)} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Kategorija</Form.Label>
                            <Form.Select value={category} onChange={(e) => setCategory(e.target.value)}>
                                <option value="">Izaberi kategoriju</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.categoryName}>{cat.categoryName}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Tagovi</Form.Label>
                            <Form.Control type="text" value={tags} onChange={(e) => setTags(e.target.value)} placeholder="tag1, tag2, tag3" />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Max kapacitet (opciono)</Form.Label>
                            <Form.Control type="number" value={maxCapacity} onChange={(e) => setMaxCapacity(e.target.value)} />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>Otkaži</Button>
                    <Button variant="primary" onClick={handleSave}>{editingEvent ? "Sačuvaj izmene" : "Dodaj događaj"}</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default EventsCreatorPage;
