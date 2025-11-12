import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import _axios from '../axiosInstance';
import '../Colors.css';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const login = async (event) => {
        event.preventDefault();
        try {
            const response = await _axios.post('/api/users/login', {
                username,
                password,
            });
            localStorage.setItem('jwt', response.data.jwt);
            navigate('/');
        } catch (error) {
            setErrorMessage('Neispravno korisničko ime ili lozinka!');
            console.error('Login failed', error);
        }
    };

    return (
        <div className="login-container first-color">
            <div className="login-card second-color">
                <h2 className="login-title fourth-color-text">Prijava na sistem</h2>

                {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}

                <form onSubmit={login}>
                    <div className="mb-3">
                        <input
                            type="text"
                            className="form-control third-color fourth-color-text"
                            placeholder="Unesite korisničko ime"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <input
                            type="password"
                            className="form-control third-color fourth-color-text"
                            placeholder="Unesite lozinku"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn-login third-color">Prijavi se</button>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
