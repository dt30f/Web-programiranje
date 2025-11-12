import { useEffect, useState } from "react";
import {Modal, Button, Row, Col, Form, Badge, Card, ListGroup} from "react-bootstrap";
import _axios from "../axiosInstance.js";
import { useNavigate } from "react-router-dom";


const EventDetail = ({ show, onClose, eventId }) => {
    const [eventData, setEventData] = useState(null);
    const [comments, setComments] = useState([]);
    const [similarEvents, setSimilarrEvents] = useState([])
    const [josAktivno, setJosAktivno] = useState(false)
    const [newComment, setNewComment] = useState({
        authorName: "",
        commentText: "",
        eventId: eventId,
        createdAt: "",
        likes: 0,
        dislikes: 0
    });
    const [ukupnoMesta, setUkupnoMesta] = useState(0)
    const [slobodnoMesta, setSlobodnoMesta] = useState(0)
    const navigate = useNavigate();

    useEffect(() => {
        if (!show || !eventId) return;

        if(!hasViewedEvent(eventId)){
            viewEvent()
            markViewedEvents(eventId)
        }

        fetchEvent();
    }, [show, eventId]);

    const viewEvent = async () => {
        await _axios(`/api/events/view_event?event_id=${eventId}`);
    }

    const fetchEvent = async () => {
        try {
            const response = await _axios(`/api/events/event?event_id=${eventId}`);
            setEventData(response.data);
            console.log(eventData.like + " " + eventData.dislike)
            // Pretpostavimo da imamo endpoint za komentare
            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);

            const rsvpRes = await _axios.get(`/api/rsvp/${eventId}`)
            setSlobodnoMesta(rsvpRes.data)

            setUkupnoMesta(eventData.capacity)
        } catch (error) {
            console.log("Greška prilikom učitavanja eventa!!!!!!", error);
        }
    };

    const handleBackgroundClick = (e) => {
        if (e.target === e.currentTarget) onClose();
    };

    const hasViewedEvent = (eventId) => {
        const viewedEvents = JSON.parse(localStorage.getItem("viewedEvents") || "[]");
        return viewedEvents.includes(eventId);
    };

    const markViewedEvents = (eventId) => {
        const viewedEvents = JSON.parse(localStorage.getItem("viewedEvents") || "[]");
        viewedEvents.push(eventId);
        localStorage.setItem("viewedEvents", JSON.stringify(viewedEvents));
    };

    const hasLikedEvent = (eventId) => {
        const likedEvents = JSON.parse(localStorage.getItem("likedEvents") || "[]");
        return likedEvents.includes(eventId);
    };

    const markEventLiked = (eventId) => {
        const likedEvents = JSON.parse(localStorage.getItem("likedEvents") || "[]");
        likedEvents.push(eventId);
        localStorage.setItem("likedEvents", JSON.stringify(likedEvents));
    };

    const hasLikedComment = (commentId) => {
        const likedComments = JSON.parse(localStorage.getItem("likedComments") || "[]");
        return likedComments.includes(commentId);
    };

    const markCommentLiked = (commentId) => {
        const likedComments = JSON.parse(localStorage.getItem("likedComments") || "[]");
        likedComments.push(commentId);
        localStorage.setItem("likedComments", JSON.stringify(likedComments));
    };

    const likeEvent = async () => {
        if (hasLikedEvent(eventId)) return;
        try {
            console.log("pozivamo like rutu")
            await _axios.post(`/api/events/${eventId}/like`);

            const response = await _axios(`/api/events/event?event_id=${eventId}`);
            setEventData(response.data);

            // Pretpostavimo da imamo endpoint za komentare
            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);
            markEventLiked(eventId);

        } catch (error) {
            console.log("Greška prilikom lajkovanja komentara", error);
        }
    }

    const procitajJos = async () => {
        if (!eventData.tags) return;
        setJosAktivno(!josAktivno)

        // Pretpostavljamo da su tagovi odvojeni zarezom
        const firstTag = eventData.tags.split(',')[0].trim();

        try {
            const res = await _axios(`/api/events/byTag3?tag=${encodeURIComponent(firstTag)}`);
            console.log(res.data);
            setSimilarrEvents(res.data)
        } catch (error) {
            console.error("Greška prilikom učitavanja srodnih događaja", error);
        }
    };

    const dislikeEvent = async () => {
        if (hasLikedEvent(eventId)) return;
        try {
            console.log("pozivamo dislike rutu")
            await _axios.post(`/api/events/${eventId}/dislike`);

            const response = await _axios(`/api/events/event?event_id=${eventId}`);
            setEventData(response.data);

            // Pretpostavimo da imamo endpoint za komentare
            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);
            markEventLiked(eventId);

        } catch (error) {
            console.log("Greška prilikom dislajkovanja komentara", error);
        }
    }

    const like = async (commentId) => {
        if (hasLikedComment(commentId)) return;
        try {
            await _axios.post(`/api/comments/${commentId}/like`);
            // Opcionalno: refetch komentara ili update lokalnog state-a
            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);
            markCommentLiked(eventId);
        } catch (error) {
            console.log("Greška prilikom lajkovanja komentara", error);
        }
    }

    const disLike = async (commentId) => {
        if (hasLikedComment(commentId)) return;
        try {
            await _axios.post(`/api/comments/${commentId}/dislike`);
            // Opcionalno: refetch komentara ili update lokalnog state-a
            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);
            markCommentLiked(eventId);

        } catch (error) {
            console.log("Greška prilikom dislajkovanja komentara", error);
        }
    }

    const changeInfo = async (newEventId) => {
        try {
            eventId = newEventId
            const response = await _axios(`/api/events/event?event_id=${eventId}`);
            setEventData(response.data);

            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);

        } catch (error) {
            console.log("Greška prilikom refresovanja kartice", error);
        }
    }

    const hideMore = () =>{
        setJosAktivno(!josAktivno)
    }


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

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        if (!newComment.authorName || !newComment.commentText) return;

        try {
            const payload = { ...newComment, eventId: eventId };
            await _axios.post(`/api/comments`, payload);

            // Reset forme
            setNewComment({ authorName: "", commentText: "", createdAt: "" });

            // Ponovo fetch eventa i komentara
            const response = await _axios(`/api/events/event?event_id=${eventId}`);
            setEventData(response.data);

            const commentsRes = await _axios(`/api/comments?event_id=${eventId}`);
            setComments(commentsRes.data);

        } catch (error) {
            console.log("Greška prilikom dodavanja komentara", error);
        }
    };
    const [rsvpPrijava, setRsvpPrijava] = useState(false)
    const [emailRsvp, setEmailRsvp] = useState("")
    const prijavaBox = ()=>{
        if(slobodnoMesta === ukupnoMesta){
            alert("Sva mesta su popunjena!")
            return;
        }
        console.log("promena na true")
        setRsvpPrijava(true);
    }
    const prijavaRsvp = ()=>{
        const payload = {
            email: emailRsvp,
            eventId: eventId
        };

        try{
            const response = _axios.post("/api/rsvp/addRsvp", payload)
            console.log(response)
        }catch (err){
            console.error(err);
            alert("Greška pri prijavi!");
        }

        setRsvpPrijava(false)
        setEmailRsvp("")
        fetchEvent()
    }

    if (!show || !eventData) return null;

    return (
        <div
            onClick={handleBackgroundClick}
            style={{
                position: "fixed",
                top: 0,
                left: 0,
                width: "100vw",
                height: "100vh",
                backgroundColor: "rgba(0,0,0,0.6)",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                zIndex: 1000,
                padding: "20px",
            }}
        >
            <div>
                {rsvpPrijava && (
                    <div
                        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center"
                        onClick={()=>setRsvpPrijava(false)} // klik sa strane zatvara
                    >
                        <div
                            className="bg-white p-6 rounded-xl shadow-lg relative w-96"
                            onClick={(e) => e.stopPropagation()} // sprečava zatvaranje kad se klikne unutra
                        >
                            {/* Dugme za zatvaranje */}
                            <button
                                className="absolute top-2 right-2 text-gray-600 hover:text-black"
                                onClick={()=>setRsvpPrijava(false)}
                            >
                                ✕
                            </button>

                            <h2 className="text-xl font-bold mb-4">Prijavi se</h2>
                            <input
                                type="email"
                                placeholder="Unesi email"
                                className="border w-full p-2 rounded mb-4"
                                value={emailRsvp}
                                onChange={(e)=>setEmailRsvp(e.target.value)}
                            />
                            <button className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700" onClick={()=>prijavaRsvp()}>
                                Prijavi se
                            </button>
                        </div>
                    </div>
                )}
            </div>
            <div
                style={{
                    width: "600px",
                    maxHeight: "90vh",
                    overflowY: "auto",
                    backgroundColor: "#1d1716", // first-color
                    borderRadius: "10px",
                    position: "relative",
                    padding: "20px",
                    boxShadow: "0 4px 20px rgba(0,0,0,0.5)",
                    color: "white",
                }}
            >
                <button
                    onClick={onClose}
                    style={{
                        position: "absolute",
                        top: "15px",
                        right: "15px",
                        background: "transparent",
                        border: "none",
                        fontSize: "24px",
                        color: "#f3bc77", // fourth-color
                        cursor: "pointer",
                    }}
                >
                    &times;
                </button>

                <h2 style={{color: "#a55233"}}>{eventData.title}</h2>
                <Badge style={{backgroundColor: "#402a23", marginBottom: "10px"}}>
                    {eventData.category}
                </Badge>
                <div style={{display: "flex", flexWrap: "wrap", gap: "5px", marginTop: "5px", marginBottom: "10px"}}>
                    {eventData.tags && eventData.tags.split(",").map((tag, index) => (
                        <Badge
                            key={index}
                            style={{
                                backgroundColor: "#a55233",
                                color: "white",
                                padding: "5px 10px",
                                borderRadius: "15px",
                                fontSize: "0.8rem"
                            }}
                            onClick={() => navigate(`/tags/${tag.trim()}`)}
                        >
                            {tag.trim()}
                        </Badge>
                    ))}
                </div>
                <p style={{color: "#f3bc77"}}>{eventData.description}</p>

                <Row className="mb-3">
                    <Col>
                        <strong>Datum i vreme održavanja:</strong> {new Date(eventData.eventDate).toLocaleString()}
                    </Col>
                    <Col>
                        <strong>Lokacija:</strong> {eventData.location}
                    </Col>
                </Row>
                <Row className="mb-3">
                    <Col>
                        <strong>Kreirano:</strong> {new Date(eventData.createdAt).toLocaleString()}
                    </Col>
                    <Col>
                        <strong>Autor:</strong> {eventData.authorName + " " + eventData.authorLastName}
                    </Col>
                </Row>

                <div style={{display: "flex", gap: "15px", marginBottom: "15px"}}>
                    <Button onClick={() => likeEvent()} style={{backgroundColor: "#a55233", border: "none"}}>Like
                        ({eventData.like})</Button>
                    <Button onClick={() => dislikeEvent()} style={{backgroundColor: "#402a23", border: "none"}}>Dislike
                        ({eventData.dislike})</Button>
                    {eventData.capacity > 0 && (
                        <Button style={{backgroundColor: "#f3bc77", color: "#1d1716", border: "none"}} onClick={()=>prijavaBox()}>
                            RSVP ({slobodnoMesta}/{eventData.capacity})
                        </Button>
                    )}
                </div>

                <hr style={{borderColor: "#402a23"}}/>

                <h5>Komentari</h5>
                <Form onSubmit={handleCommentSubmit} className="mb-3">
                    <Form.Control
                        type="text"
                        placeholder="Vaše ime"
                        value={newComment.authorName}
                        onChange={(e) => setNewComment({...newComment, authorName: e.target.value})}
                        className="mb-2"
                        style={{backgroundColor: "#402a23", color: "white", border: "none"}}
                    />
                    <Form.Control
                        as="textarea"
                        rows={2}
                        placeholder="Vaš komentar"
                        value={newComment.commentText}
                        onChange={(e) => setNewComment({...newComment, commentText: e.target.value})}
                        style={{backgroundColor: "#402a23", color: "white", border: "none"}}
                        className="mb-2"
                    />
                    <Button type="submit" style={{backgroundColor: "#a55233", border: "none"}}>
                        Dodaj komentar
                    </Button>
                </Form>

                {comments.map((comment) => (
                    <Card key={comment.id} style={{backgroundColor: "#402a23", marginBottom: "10px", color: "white"}}>
                        <Card.Body>
                            <Card.Title style={{color: "#f3bc77"}}>{comment.authorName}</Card.Title>
                            <Card.Text>{comment.commentText}</Card.Text>
                            <small>Kreirano: {formatDate(comment.createdAt)}</small>
                            <div style={{marginTop: "5px", display: "flex", gap: "10px"}}>
                                <Button onClick={() => like(comment.id)} size="sm"
                                        style={{backgroundColor: "#a55233", border: "none"}}>
                                    Like ({comment.likes})
                                </Button>
                                <Button onClick={() => disLike(comment.id)} size="sm"
                                        style={{backgroundColor: "#402a23", border: "none"}}>
                                    Dislike ({comment.dislikes})
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                ))}
                <div>
                    {!josAktivno && (
                        <p onClick={() => procitajJos()}>Slični eventi</p>
                    )}
                    {josAktivno && (
                        <>
                            {similarEvents.map((similarEvent) =>(
                                <Card onClick={() => changeInfo(similarEvent.id)} style={{ padding: "5px", backgroundColor: "white" }}>
                                    <ListGroup variant="flush">
                                        <ListGroup.Item style={{backgroundColor: "#f3bc77"}}>{similarEvent.title}</ListGroup.Item>
                                        <ListGroup.Item style={{backgroundColor: "#f3bc77"}}>{similarEvent.description}</ListGroup.Item>
                                    </ListGroup>
                                </Card>

                            ))}
                        <p onClick={() => hideMore()}>Sakrij</p>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default EventDetail;
