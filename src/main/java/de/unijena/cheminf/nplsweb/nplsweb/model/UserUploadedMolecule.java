package de.unijena.cheminf.nplsweb.nplsweb.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="user_uploaded_molecule" , indexes = {  @Index(name = "IDXI", columnList = "inchikey"  )})
public class UserUploadedMolecule {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer umol_id;

    private String uu_id;

    @Column(length=1200)
    private String smiles;

    private String inchikey;

    private Date submissionDate;


    private Integer is_in_any_source;

    private String sources;

    private Integer addedToMolecule;

    private String depictionLocation;


    private Integer sugar_free_atom_number;

    private Double npl_score;

    private Double npl_sugar_score;

    private Double sml_score;

    private Double sml_sugar_score;

    private Integer atom_number;

    private String sessionid;




    @PrePersist
    protected void onCreate() {
        submissionDate = new Date();
    }


    public Integer getUmol_id() {
        return umol_id;
    }

    public void setUmol_id(Integer umol_id) {
        this.umol_id = umol_id;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }


    public Integer getAddedToMolecule() {
        return addedToMolecule;
    }

    public void setAddedToMolecule(Integer addedToMolecule) {
        this.addedToMolecule = addedToMolecule;
    }

    public Double getNpl_score() {
        return npl_score;
    }

    public void setNpl_score(Double npl_score) {
        this.npl_score = npl_score;
    }

    public Double getNpl_sugar_score() {
        return npl_sugar_score;
    }

    public void setNpl_sugar_score(Double npl_sugar_score) {
        this.npl_sugar_score = npl_sugar_score;
    }

    public Double getSml_score() {
        return sml_score;
    }

    public void setSml_score(Double sml_score) {
        this.sml_score = sml_score;
    }

    public Double getSml_sugar_score() {
        return sml_sugar_score;
    }

    public void setSml_sugar_score(Double sml_sugar_score) {
        this.sml_sugar_score = sml_sugar_score;
    }

    public Integer getAtom_number() {
        return atom_number;
    }

    public void setAtom_number(Integer atom_number) {
        this.atom_number = atom_number;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getUu_id() {
        return uu_id;
    }

    public void setUu_id(String uu_id) {
        this.uu_id = uu_id;
    }

    public String getDepictionLocation() {
        return depictionLocation;
    }

    public void setDepictionLocation(String depictionLocation) {
        this.depictionLocation = depictionLocation;
    }


    public Integer getSugar_free_atom_number() {
        return sugar_free_atom_number;
    }

    public void setSugar_free_atom_number(Integer sugar_free_atom_number) {
        this.sugar_free_atom_number = sugar_free_atom_number;
    }

    public Integer getIs_in_any_source() {
        return is_in_any_source;
    }

    public void setIs_in_any_source(Integer is_in_any_source) {
        this.is_in_any_source = is_in_any_source;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getInchikey() {
        return inchikey;
    }

    public void setInchikey(String inchikey) {
        this.inchikey = inchikey;
    }
}
