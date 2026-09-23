INSERT INTO ordenes_trabajo (id, cliente_id, servicio_id, repuesto_id, estado, direccion, tecnico_asignado_id, fecha_creacion, fecha_actualizacion) VALUES
(1, 'CLI001', 1, 1, 'CREADA', 'Edificio Los Alerces, Providencia', NULL, NOW(), NOW()),
(2, 'CLI002', 2, 2, 'ASIGNADA', 'PyME Comercializadora Sur, Santiago Centro', 'TEC001', NOW(), NOW()),
(3, 'CLI003', 3, NULL, 'EN_EJECUCION', 'Condominio Las Palmas, Las Condes', 'TEC002', NOW(), NOW()),
(4, 'CLI004', 4, NULL, 'CERRADA', 'Fábrica Textil Andes, Maipú', 'TEC003', NOW(), NOW()),
(5, 'CLI005', 5, 5, 'CANCELADA', 'Oficinas Corporativas Norte, Vitacura', NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
