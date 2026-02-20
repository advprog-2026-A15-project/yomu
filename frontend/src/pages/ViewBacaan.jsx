import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

export default function ViewBacaan() {
    const [bacaans, setBacaans] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/bacaan").then(res => res.json()).then(setBacaans);
    }, []);

    return (
        <div className="page-container">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '30px' }}>
                <h1 style={{ color: 'var(--lavender)' }}>Yomu: Daftar Bacaan</h1>
                <Link to="/create">
                    <button className="btn" style={{ backgroundColor: 'var(--green)', color: 'var(--base)' }}>+ Tambah</button>
                </Link>
            </div>

            <table className="macchiato-table">
                <thead>
                <tr>
                    <th className="col-id">UUID</th>
                    <th>Judul</th>
                    <th>Konten</th>
                    <th style={{ width: '180px' }}>Aksi</th>
                </tr>
                </thead>
                <tbody>
                {bacaans.map(b => (
                    <tr key={b.id}>
                        <td className="col-id" title={b.id}>{b.id}</td>
                        <td title={b.judul}><strong>{b.judul.length > 40
                            ? b.judul.substring(0, 40) + "..."
                            : b.judul}</strong></td>
                        <td title={b.isiTeks}>{b.isiTeks.length > 80
                            ? b.isiTeks.substring(0, 80) + "..."
                            : b.isiTeks}</td>
                        <td style={{ textAlign: 'center' }}>
                            <Link to={`/edit/${b.id}`}>
                                <button className="btn" style={{ backgroundColor: 'var(--blue)', color: 'var(--base)', marginRight: '8px' }}>Edit</button>
                            </Link>
                            <Link to={`/delete/${b.id}`}>
                                <button className="btn" style={{ backgroundColor: 'var(--red)', color: 'var(--base)' }}>Hapus</button>
                            </Link>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}