import React, { useState } from 'react';
import axios from 'axios';

axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080'; 

function LoginPage() {
    const [isLoginView, setIsLoginView] = useState(true);
    
    const [formData, setFormData] = useState({
        nickname: '',
        email: '',
        password: '',
        repeatedPassword: ''
    });

    const [statusMessage, setStatusMessage] = useState('');

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleLogin = async (e) => {
        e.preventDefault(); 
        setStatusMessage('');

        try {
            await axios.post('/api/users/login', {
                nickname: formData.nickname,
                password: formData.password
            });
            
            setStatusMessage('Zalogowano pomyślnie!');
            
        } catch (error) {
            if (error.response?.status === 403 && error.response?.data?.error === 'UNVERIFIED_EMAIL') {
                setStatusMessage('Account unverified - please check your email for code');
            } else {
                setStatusMessage(error.response?.data?.error || 'Login error');
            }
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setStatusMessage('');

        try {
            await axios.post('/api/users/register', {
                nickname: formData.nickname,
                email: formData.email,
                password: formData.password,
                repeatedPassword: formData.repeatedPassword
            });

            setStatusMessage('Account registred - please check your email for verification code');
            setIsLoginView(true);
            
        } catch (error) {
            setStatusMessage(error.response?.data?.message || 'Register error');
        }
    };

    return (
        <div className={`login-screen ${isLoginView ? 'bg-[url(/loginBg.png)]' : 'bg-[url(/registerBg.png)]'}`}>
            <div className={`loginElement ${isLoginView ? 'bg-loginBlue/50' : 'bg-registerBlue/50' }`}>
                
                <div className="login-header">
                    <span className="login-badge">2FA</span>
                    <span className="login-title1">
                        {isLoginView ? 'Login' : 'Register'}
                        <span className="login-title2">
                            Page
                        </span>
                    </span>
                    
                </div>
                
                <form onSubmit={isLoginView ? handleLogin : handleRegister} className="login-form">
                    
                    <div className="form-group">
                        <label className="form-label">Email</label>
                        <input 
                            type="email" 
                            name="email" 
                            className="form-input"
                            placeholder="name@example.com"
                            value={formData.email} 
                            onChange={handleInputChange} 
                            required 
                        />
                    </div>

                    {!isLoginView && (
                        
                        <div className="form-group">
                        <label className="form-label">Username</label>
                        <input 
                                type="text" 
                                name="nickname" 
                                className="form-input"
                                placeholder="ExampleMail@example.com"
                                value={formData.nickname} 
                                onChange={handleInputChange} 
                                required 
                            />
                        </div>
                    )}

                    <div className="form-group">
                        <label className="form-label">Password</label>
                        <div className="password-input-wrapper">
                            <input 
                                type="password" 
                                name="password" 
                                className="form-input"
                                placeholder="YourSuperSecretPassword123"
                                value={formData.password} 
                                onChange={handleInputChange} 
                                required 
                            />
                        </div>
                    </div>

                    {!isLoginView && (
                        <div className="form-group">
                            <label className="form-label">Repeat Password</label>
                            <input 
                                type="password" 
                                name="repeatedPassword" 
                                className="form-input"
                                placeholder="YourSuperSecretPassword123"
                                value={formData.repeatedPassword} 
                                onChange={handleInputChange} 
                                required 
                            />
                        </div>
                    )}
                </form>
                 <div className="login-footer">
                        <button type="submit" className="login-button">
                            {isLoginView ? 'Login' : 'Register'}
                        </button>

                        <p className="register-text">
                            {isLoginView ? (
                                <>New to this service? - <button type="button" className="link-button" onClick={() => { setIsLoginView(false); setStatusMessage(''); }}>Click here to create an Account</button></>
                            ) : (
                                <>Already have an Account? - <button type="button" className="link-button" onClick={() => { setIsLoginView(true); setStatusMessage(''); }}>Click here to log in</button></>
                            )}
                        </p>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;