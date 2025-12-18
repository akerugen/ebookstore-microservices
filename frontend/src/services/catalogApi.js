import { api } from "./httpClient";

export const catalogApi = {
  async getBooks() {
    const res = await api.get("/catalog/books");
    return res.data;
  },
  async getBook(id) {
    const res = await api.get(`/catalog/books/${id}`);
    return res.data;
  },
  async createBook(payload) {
    const res = await api.post("/catalog/books", payload);
    return res.data;
  },
  async updateBook(id, payload) {
    const res = await api.put(`/catalog/books/${id}`, payload);
    return res.data;
  },
  async deleteBook(id) {
    await api.delete(`/catalog/books/${id}`);
  },
  async getAuthors() {
    const res = await api.get("/catalog/authors");
    return res.data;
  },
  async getAuthor(id) {
    const res = await api.get(`/catalog/authors/${id}`);
    return res.data;
  },
  async getBooksByAuthor(authorId) {
    const res = await api.get(`/catalog/books/filter/author/${authorId}`);
    return res.data;
  },
  async createAuthor(payload) {
    const res = await api.post("/catalog/authors", payload);
    return res.data;
  },
  async updateAuthor(id, payload) {
    const res = await api.put(`/catalog/authors/${id}`, payload);
    return res.data;
  },
  async deleteAuthor(id) {
    await api.delete(`/catalog/authors/${id}`);
  },
  async getGenres() {
    const res = await api.get("/catalog/genres");
    return res.data;
  },
  async getBookStatuses() {
    const res = await api.get("/catalog/book-statuses");
    return res.data;
  }
};



