import React, { useState } from 'react';
import axios from 'axios';

axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080';

function LoginPage() {
    const [viewMode, setViewMode] = useState('login'); 
    
    const [formData, setFormData] = useState({
        nickname: '',
        email: '',
        password: '',
        repeatedPassword: '',
        code: ''
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
            
            setStatusMessage('Logged in!');
        } catch (error) {
            if (error.response?.status === 403 && error.response?.data?.error === 'UNVERIFIED_EMAIL') {
                setStatusMessage('Please verify your account.');
                setViewMode('verify');
            } else {
                setStatusMessage(error.response?.data?.error || 'Login error, please try again');
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

            setStatusMessage('Account registred, please check your email for verification code.');
            setViewMode('verify');
            
        } catch (error) {
            setStatusMessage(error.response?.data?.message || 'Błąd rejestracji');
        }
    };

    const handleVerifyCode = async (e) => {
        e.preventDefault();
        setStatusMessage('');

        try {
            await axios.post('/api/users/verify', {
                nickname: formData.nickname,
                code: formData.code
            });

            setStatusMessage('Account verified, you can login now.');
            setViewMode('login');
        } catch (error) {
            setStatusMessage(error.response?.data?.error || 'Verification code error');
        }
    };

    const getFormSubmitHandler = () => {
        if (viewMode === 'login') return handleLogin;
        if (viewMode === 'register') return handleRegister;
        return handleVerifyCode;
    };

    return (
        <div className={`login-screen ${viewMode === 'login' ? 'bg-[url(/loginBg.png)]' : 'bg-[url(/registerBg.png)]'}`}>
            <div className={`loginElement ${viewMode === 'login' ? 'bg-loginBlue/50' : 'bg-registerBlue/50'}`}>
                
                <div className="login-header">
                    <span className="login-badge">2FA</span>
                    <span className="login-title1">
                        {viewMode === 'login' && 'Login'}
                        {viewMode === 'register' && 'Register'}
                        {viewMode === 'verify' && 'Verify 2FA'}
                        <span className="login-title2"> Page</span>
                    </span>
                </div>
                
                {statusMessage && (
                    <div className="status-message text-center text-sm font-semibold mb-4 text-amber-300">
                        {statusMessage}
                    </div>
                )}

                <form onSubmit={getFormSubmitHandler()} className="login-form">
                    
                    {viewMode === 'verify' ? (
                        <>
                            <div className="form-group">
                                <label className="form-label">Nickname</label>
                                <input 
                                    type="text" 
                                    name="nickname" 
                                    className="form-input opacity-70 cursor-not-allowed"
                                    value={formData.nickname} 
                                    onChange={handleInputChange} 
                                    required 
                                    readOnly
                                />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Verification Code</label>
                                <input 
                                    type="text" 
                                    name="code" 
                                    className="form-input tracking-widest text-center text-xl font-bold"
                                    placeholder="123456"
                                    maxLength={6}
                                    value={formData.code} 
                                    onChange={handleInputChange} 
                                    required 
                                    autoFocus
                                />
                            </div>
                        </>
                    ) : (
                        /* WIDOKI LOGOWANIA I REJESTRACJI */
                        <>
                            <div className="form-group">
                                <label className="form-label">Nickname</label>
                                <input 
                                    type="text" 
                                    name="nickname" 
                                    className="form-input"
                                    placeholder="JanKowalski"
                                    value={formData.nickname} 
                                    onChange={handleInputChange} 
                                    required 
                                />
                            </div>
                           
                            {viewMode === 'register' && (
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

                            {viewMode === 'register' && (
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
                        </>
                    )}

                    <div className="login-footer mt-6">
                        <button type="submit" className="login-button w-full">
                            {viewMode === 'login' && 'Login'}
                            {viewMode === 'register' && 'Register'}
                            {viewMode === 'verify' && 'Verify Code'}
                        </button>

                        <p className="register-text mt-4">
                            {viewMode === 'login' && (
                                <>New to this service? - <button type="button" className="link-button" onClick={() => { setViewMode('register'); setStatusMessage(''); }}>Click here to create an Account</button></>
                            )}
                            {viewMode === 'register' && (
                                <>Already have an Account? - <button type="button" className="link-button" onClick={() => { setViewMode('login'); setStatusMessage(''); }}>Click here to log in</button></>
                            )}
                            {viewMode === 'verify' && (
                                <button type="button" className="link-button" onClick={() => { setViewMode('login'); setStatusMessage(''); }}>Back to Login</button>
                            )}
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default LoginPage;