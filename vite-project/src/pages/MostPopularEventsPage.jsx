import {useContext, useEffect, useState} from "react";
import _axios from "../axiosInstance.js";
import {Col, Row} from "react-bootstrap";
import Card from "react-bootstrap/Card";
import {SearchContext} from "../components/SearchContext.jsx";
import EventDetail from "../components/EventDetail.jsx";
import MostReactions from "../components/MostReactions.jsx";

const MostPopularEventsPage = () =>{
    const [events, setEvents] = useState([]);
    const { searchTerm } = useContext(SearchContext);
    const [showModal, setShowModal] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);

    useEffect(() => {
        const fetchEvents = async () => {
            try{
                const response = await _axios("/api/events/popular")
                console.log(response)
                setEvents(response.data)
            }catch (error){
                console.log("Greška prilikom učitavanja eventa", error)
            }
        }
        fetchEvents()
    }, []);
    const filteredEvents = events.filter(
        (event) =>
            event.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            event.description.toLowerCase().includes(searchTerm.toLowerCase())
    );
    return(
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
                            <Card.Header>{event.category}</Card.Header>
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
                        </Card>
                    </Col>
                ))}
            </Row>
        </>
    )
}

export default MostPopularEventsPage