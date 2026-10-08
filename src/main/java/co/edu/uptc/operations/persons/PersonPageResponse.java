package co.edu.uptc.operations.persons;

import java.util.List;

public class PersonPageResponse {

    private final String mensaje;
    private final String vm;
    private final String container;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final List<Person> content;

    public PersonPageResponse(
            String mensaje,
            String vm,
            String container,
            int page,
            int size,
            long totalElements,
            int totalPages,
            List<Person> content) {
        this.mensaje = mensaje;
        this.vm = vm;
        this.container = container;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.content = content;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getVm() {
        return vm;
    }

    public String getContainer() {
        return container;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Person> getContent() {
        return content;
    }
}
