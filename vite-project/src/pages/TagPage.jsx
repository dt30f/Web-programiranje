import { useParams } from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import _axios from "../axiosInstance";
import {Col, Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";
import {SearchContext} from "../components/SearchContext.jsx";
import EventDetail from "../components/EventDetail.jsx";
import MostReactions from "../components/MostReactions.jsx";

const TagPage = () => {
    const { tagName } = useParams();
    const { searchTerm } = useContext(SearchContext);
    const [events, setEvents] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);

    useEffect(() => {
        const fetchEventsByTag = async () => {
            try {
                const res = await _axios(`/api/events/byTag?tag=${encodeURIComponent(tagName)}`);
                setEvents(res.data);
            } catch (error) {
                console.log("Greška prilikom fetchovanja eventa po tagu", error);
            }
        };
        fetchEventsByTag();
    }, [tagName, showModal]);
    const filteredEvents = events.filter(
        (event) =>
            event.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            event.description.toLowerCase().includes(searchTerm.toLowerCase())
    );
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

    return (
        <div>
            <h2 style={{color: "#e47e59"}}>{tagName}</h2>
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
                    zIndex: 9999
                }}
            >
                <MostReactions onEventClick={(eventId) => {
                    setSelectedEventId(eventId)
                    setShowModal(true)
                }}/>
            </div>
            <Row className="g-3">
                {filteredEvents.map((event) => (
                    <Col key={event.id} xs={12} className="d-flex justify-content-center">
                        <Card className="text-center" style={{width: "400px", backgroundColor: "#f3bc77"}}>
                            <Card.Header
                                style={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
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
                                        fontSize: "0.9rem"
                                    }}
                                >
        {event.views}
    </span>
                            </Card.Header>
                            <Card.Body>
                                <Card.Title onClick={() => {
                                    setSelectedEventId(event.id);
                                    setShowModal(true)
                                }
                                }>{event.title}</Card.Title>
                                <Card.Text>
                                    {event.description}
                                </Card.Text>
                                {/*<Button variant="primary">Go somewhere</Button>*/}
                            </Card.Body>
                            <Card.Footer className="text-muted">{formatDate(event.createdAt)}</Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>
        </div>
    );
};

export default TagPage;
