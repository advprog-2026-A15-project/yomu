import { useState, useEffect } from 'react'
import './App.css'

function App() {
    const [data, setData] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        // Menembak API Spring Boot
        fetch('http://localhost:8080/api/test')
            .then((response) => response.json())
            .then((data) => {
                setData(data)
                setLoading(false)
            })
            .catch((error) => {
                console.error("Gagal nyambung ke Backend:", error)
                setLoading(false)
            })
    }, [])

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h1>Tes Koneksi Yomu 🚀</h1>

            {loading ? (
                <p>Menghubungi Backend...</p>
            ) : data ? (
                <div style={{ padding: '20px', border: '2px solid green', borderRadius: '10px' }}>
                    <h2>✅ {data.status}</h2>
                    <p>{data.message}</p>
                </div>
            ) : (
                <div style={{ padding: '20px', border: '2px solid red', borderRadius: '10px' }}>
                    <h2>❌ Gagal Tersambung</h2>
                    <p>Pastikan Spring Boot sudah berjalan di port 8080.</p>
                </div>
            )}
        </div>
    )
}

export default App