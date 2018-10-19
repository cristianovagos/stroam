DELETE FROM Services_Users;
DELETE FROM Services;
DELETE FROM Users;

#Insert Services
INSERT INTO Services VALUES("977e85d9-8982-4a90-abd0-62465afd07ef", "serviceTestE1", "/serviceTestE1", 1);
INSERT INTO Services VALUES("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", "serviceTestE2", "/serviceTestE2", 1);
INSERT INTO Services VALUES("d6999660-d263-48ad-abab-f5c63e8a828a", "serviceTestD3", "/serviceTestD3", 0);

#Insert Users
INSERT INTO Users VALUES("ed2305e7-c101-42be-b791-bc1ec9fdc1fb", "625d1f59-6b9a-4798-b24e-4a8cdfd6fd5f", "User0", "teste0@teste.com", "00351912345670", 1, 0, 0, 1);
INSERT INTO Users VALUES("d3ae443f-c048-4ac0-81f1-efa57b8d345b", "116d8f02-80d2-4add-a9ac-be82a8e663c7", "User1", "teste1@teste.com", "00351912345671", 0, 1, 0, 1);
INSERT INTO Users VALUES("6ef5b662-ef6d-4166-acd1-607e71cb9a93", "be68dac7-ab5e-4609-ac0a-3e6e4595491c", "User2", "teste2@teste.com", "00351912345672", 0, 0, 1, 1);
INSERT INTO Users VALUES("56e48477-31b1-491b-80fa-c7e88964ce9a", "5433fc97-6c12-491d-bdd1-9c9b874c6213", "UserD3", "teste3@teste.com", "00351912345673", 1, 1, 1, 0);
INSERT INTO Users VALUES("fa6b601e-a6e2-4803-8d22-5f72e4c60306", "d3e36549-3c5f-4892-b257-8f820f042fa6", "User4", "teste4@teste.com", "00351912345674", 1, 0, 0, 1);
INSERT INTO Users VALUES("c4235f22-8259-45a3-8b9e-125aed70ed95", "308254b8-3dc5-41df-afba-ebcb91aa02f1", "User5", "teste5@teste.com", "00351912345675", 0, 1, 0, 1);
INSERT INTO Users VALUES("dcc80e20-59d7-49ea-aff2-fc62844fefd9", "0c8f53db-4ef6-4088-badc-7f4fa176a710", "User6", "teste6@teste.com", "00351912345676", 0, 0, 1, 1);
INSERT INTO Users VALUES("7e8d0dcf-e531-4b19-aac9-7876002ec68d", "01acfc54-e313-42cf-a570-8890970a8a66", "UserD7", "teste7@teste.com", "00351912345677", 1, 1, 1, 0);
INSERT INTO Users VALUES("4e8a00ee-738b-4691-853e-70f0c8d217a3", "45d3c090-83b2-4326-b396-b820c41350a9", "User8", "teste8@teste.com", "00351912345678", 1, 0, 0, 1);
INSERT INTO Users VALUES("25c672bd-211b-4bfc-bdfe-95de27281098", "c894251e-490d-4db7-b2e5-871c98ecc69b", "User9", "teste9@teste.com", "00351912345679", 0, 1, 0, 1);
INSERT INTO Users VALUES("8b428009-aa99-45ad-8c27-b1ea3673d420", "8002aea6-d0b3-4407-90bf-03970b080b86", "User10", "teste10@teste.com", "00351912345610", 0, 0, 1, 1);
INSERT INTO Users VALUES("55822e82-0614-448d-a020-fc7d0a55433e", "25c672bd-aa99-8d22-31b1-f5c63e8a828a", "UserD11", "teste11@teste.com", "00351912345611", 1, 1, 1, 0);

#Insert Services_Users
INSERT INTO Services_Users VALUES("977e85d9-8982-4a90-abd0-62465afd07ef", "ed2305e7-c101-42be-b791-bc1ec9fdc1fb");
INSERT INTO Services_Users VALUES("977e85d9-8982-4a90-abd0-62465afd07ef", "d3ae443f-c048-4ac0-81f1-efa57b8d345b");
INSERT INTO Services_Users VALUES("977e85d9-8982-4a90-abd0-62465afd07ef", "6ef5b662-ef6d-4166-acd1-607e71cb9a93");
INSERT INTO Services_Users VALUES("977e85d9-8982-4a90-abd0-62465afd07ef", "56e48477-31b1-491b-80fa-c7e88964ce9a");
INSERT INTO Services_Users VALUES("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", "fa6b601e-a6e2-4803-8d22-5f72e4c60306");
INSERT INTO Services_Users VALUES("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", "c4235f22-8259-45a3-8b9e-125aed70ed95");
INSERT INTO Services_Users VALUES("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", "dcc80e20-59d7-49ea-aff2-fc62844fefd9");
INSERT INTO Services_Users VALUES("3e260f74-ea58-4c48-afa0-1e1f5630f8ae", "7e8d0dcf-e531-4b19-aac9-7876002ec68d");
INSERT INTO Services_Users VALUES("d6999660-d263-48ad-abab-f5c63e8a828a", "4e8a00ee-738b-4691-853e-70f0c8d217a3");
INSERT INTO Services_Users VALUES("d6999660-d263-48ad-abab-f5c63e8a828a", "25c672bd-211b-4bfc-bdfe-95de27281098");
INSERT INTO Services_Users VALUES("d6999660-d263-48ad-abab-f5c63e8a828a", "8b428009-aa99-45ad-8c27-b1ea3673d420");
INSERT INTO Services_Users VALUES("d6999660-d263-48ad-abab-f5c63e8a828a", "55822e82-0614-448d-a020-fc7d0a55433e");