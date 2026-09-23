INSERT INTO repuestos (id, codigo, nombre, descripcion, stock, precio, activo) VALUES 
(1, 'REP001', 'Disyuntor Termomagnético 20A', 'Disyuntor bipolar para tableros residenciales', 50, 15000, true),
(2, 'REP002', 'Tablero de Distribución 12 Polos', 'Tablero embutido para distribución eléctrica', 10, 12000, true),
(3, 'REP003', 'Rollo Cable THHN 12 AWG', 'Cable eléctrico de cobre 100 metros color rojo', 30, 85000, true),
(4, 'REP004', 'Interruptor Diferencial 2x40A', 'Protección contra fugas de corriente (Salvavidas)', 25, 35000, true),
(5, 'REP005', 'Tubo Conduit PVC 20mm', 'Tubo rígido para canalización eléctrica (3 metros)', 100, 3500, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO servicios (id, nombre, descripcion, tarifa, activo) VALUES
(1, 'Mantención Preventiva de Tablero', 'Revisión y reapriete de conexiones en tablero eléctrico principal', 150000, true),
(2, 'Diagnóstico de Cortocircuito', 'Inspección de fallas y fugas eléctricas en instalaciones', 70000, true),
(3, 'Instalación de Punto de Red', 'Canalización y cableado para nuevo punto eléctrico', 75000, true),
(4, 'Certificación TE1 SEC', 'Inspección técnica y declaración de instalación eléctrica', 350000, true),
(5, 'Cambio de Luminarias LED', 'Reemplazo de focos tradicionales por iluminación LED industrial/comercial', 80000, true)
ON CONFLICT (id) DO NOTHING;
