import _axios from "../axiosInstance";
import { useContext, useEffect, useState } from "react";
import Card from "react-bootstrap/Card";
import { Col, Row, Button } from "react-bootstrap";
import { SearchContext } from "./SearchContext.jsx";
import EventDetail from "./EventDetail.jsx";
import MostReactions from "./MostReactions.jsx";

const Home = () => {
    const [events, setEvents] = useState([]);
    const { searchTerm } = useContext(SearchContext);
    const [showModal, setShowModal] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);
    //const [totalEvents, setTotalEvents] = useState(0)

    const [currentPage, setCurrentPage] = useState(1);
    const eventsPerPage = 5;

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        if (isNaN(date)) return "Nepoznat datum";
        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");
        return `${day}.${month}.${year}. ${hours}:${minutes}`;
    };

    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const response = await _axios.get(`/api/events/${currentPage}/${eventsPerPage}`);
                setEvents(response.data);

               // const response2 = await _axios.get("/api/events/pages")
               // setTotalEvents(response2)

            } catch (error) {
                console.log("Greška prilikom učitavanja eventa!!!", error);
            }
        };
        fetchEvents();

        if (localStorage.getItem("eventDetail") !== "-1" || localStorage.getItem("eventDetail") == null) {
            setSelectedEventId(parseInt(localStorage.getItem("eventDetail")));
            setShowModal(true);
            localStorage.setItem("eventDetail", "-1");
        }
    }, [showModal, currentPage]);

    const filteredEvents = events.filter(
        (event) =>
            event.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            event.description.toLowerCase().includes(searchTerm.toLowerCase())
    );


    const totalPages = 2;

    return (
        <>
            <EventDetail
                show={showModal}
                onClose={() => setShowModal(false)}
                eventId={selectedEventId}
            />
            <div
                style={{
                    position: "fixed",
                    top: "70px",
                    right: "30px",
                    zIndex: 9999,
                }}
            >
                <MostReactions
                    onEventClick={(eventId) => {
                        setSelectedEventId(eventId);
                        setShowModal(true);
                    }}
                />
            </div>

            <Row className="g-3">
                {filteredEvents.map((event) => (
                    <Col key={event.id} xs={12} className="d-flex justify-content-center">
                        <Card className="text-center" style={{ width: "400px", backgroundColor: "#f3bc77" }}>
                            <Card.Header
                                style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}
                            >
                                {event.category}
                                <span
                                    style={{
                                        display: "inline-flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        width: "25px",
                                        height: "25px",
                                        borderRadius: "50%",
                                        backgroundColor: "#f3bc77",
                                        color: "#1d1716",
                                        fontWeight: "bold",
                                        fontSize: "0.9rem",
                                    }}
                                >
                                    {event.views}
                                </span>
                            </Card.Header>
                            <Card.Body>
                                <Card.Title
                                    onClick={() => {
                                        setSelectedEventId(event.id);
                                        setShowModal(true);
                                    }}
                                >
                                    {event.title}
                                </Card.Title>
                                <Card.Text>{event.description}</Card.Text>
                            </Card.Body>
                            <Card.Footer className="text-muted">{formatDate(event.createdAt)}</Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>

            <div className="d-flex justify-content-center my-3">
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
        </>
    );
};

export default Home;
