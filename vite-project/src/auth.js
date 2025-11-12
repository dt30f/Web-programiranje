// auth.js
// Funkcije za autentikaciju korisnika -> proverava JWT

// Proverava da li je korisnik ulogovan
export const isAuthenticated = () => {
    const jwt = localStorage.getItem("jwt");
    if (!jwt) return false;

    try {
        const parts = jwt.split(".");
        if (parts.length !== 3) return false; // JWT mora imati tri dela

        const payload = JSON.parse(atob(parts[1])); // Dekodiramo payload

        if (!payload.exp) return false; // Ako nema exp, nije validan

        return Date.now() < payload.exp * 1000; // Proverava da li je token istekao
    } catch (error) {
        console.error("Greška pri dekodiranju JWT tokena:", error);
        return false; // Ako dođe do greške, vraća false
    }
};

// Dohvata korisnika i njegovu rolu iz JWT
export const getUserRole = () => {
    const jwt = localStorage.getItem("jwt");
    if (!jwt) return null;

    try {
        const payload = JSON.parse(atob(jwt.split(".")[1]));
        return payload.role || null; // Vraća rolu korisnika ili null
    } catch (error) {
        console.error("Greška pri dekodiranju JWT role:", error);
        return null;
    }
};

// Provera da li je korisnik admin
export const isAdmin = () => {
    return getUserRole() === "ADMIN";
};

// Provera da li je korisnik creator
export const isCreator = () => {
    return getUserRole() === "EVENT_CREATOR";
};

// Funkcija za odjavu korisnika
export const logout = (navigate) => {
    localStorage.removeItem("jwt");
    navigate("/login");
};
